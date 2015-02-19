#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"


typedef enum { role_ping_out = 1, role_pong_back } role_e;
const char* role_friendly_name[] = { "invalid", "Ping out", "Pong back"};

//Start: Controller Specific Values

	int loopsPSec;

//End: Controller Specific Values


//Start: Mutual but different.

	const uint64_t pipes[2] = { 0xF0F0F0F0E1LL, 0xF0F0F0F0D2LL };

	//Initialize Radio
	RF24 radio(9,10);

	//Indicator LEDs
	const int GLED = -1;
	const int RLED = -1;

//End: Mutual but different.


//Start: mutual values.

	//Variables
		//Transmission Values
		const int valuesLen = 16;
		const int desOrientLen = 4;
		const int curOrientLen = 4;
		const uint64_t contSend = 0xE8E8F0F0E1LL;
		const uint64_t quadSend = 0xF0F0F0F0D2LL;

		//Message Values
		const int desOrientStart = 30001;
		const	int endValue = 30002;
		const int curOrientStart = 30003;

		/* Desired Orientation
		-0: thrust
		-1: roll
		-2: pitch
		-3: yaw
		*/
		int desOrient[desOrientLen];

		/* Actual Orientation		TODO -8: GyTime.
		-0: height
		-1: AcX
		-2: AcY
		-3: AcZ
		-4: Tmp
		-5: GyX
		-6: GyY
		-7: GyZ
		*/
		int curOrient[curOrientLen];

		//Time tracking.
		unsigned long time;
		unsigned long lastTimeValue;

		//Used by getValues().
		int incVal;
		int checkSum;
		boolean mesStart;


	//Methods
		//Used to avoid turning off/on LEDs that don't exist on a module.
		void ledOnOff(int LED, boolean onOff){
			if(LED == -1){
				return;
			} else {
				digitalWrite(LED, onOff);
			}
		}

		void getValues(){
			int values[valuesLen];
			
			radio.read(values, sizeof(values));
			
			int checkSum = values[0];
			
			//Desired Orientation Message
			if(values[0] == desOrientStart){
				int data[desOrientLen];
				for(int i=0; i<desOrientLen; i++){
					data[i] = values[i+1];
					checkSum += values[i+1];
				}
				checkSum += values[desOrientLen+1];
				if(checkSum == values[desOrientLen+2]){
					for(int i=0; i<desOrientLen; i++){
						desOrient[i] = data[i];
					}
				} else {
					ledOnOff(RLED, HIGH);
				}
			} 
			//Current Orientation Message
			else if(values[0] == curOrientStart){
				int data[curOrientLen];
				for(int i=0; i<curOrientLen; i++){
					data[i] = values[i+1];
					checkSum += values[i+1];
				}
				checkSum += values[curOrientLen+1];
				if(checkSum == values[curOrientLen+2]){
					for(int i=0; i<curOrientLen; i++){
						curOrient[i] = data[i];
					}
				} else {
					ledOnOff(RLED, HIGH);
				}
			} 
			//Unexpected Message
			else {
				ledOnOff(RLED, HIGH);
			}
		}

		void sendValues(int startValue, int data[], int dataLen){
			//First, stop listening so we can talk.
			radio.stopListening();

			int values[16]; //Current max amount of data that can be sent over Wireless Module.
			int checkSum = 0;

			//Initialize the array values for the write loop.   Is there a better way to do this?
			values[0] = startValue;
			for(int i=0; i<dataLen; i++){
				values[i+1] = data[i];
			}
			values[dataLen+1] = endValue;
			//Create _checkSum.
			for(int i=0; i<dataLen+2; i++){
				checkSum += values[i];
			}
			values[dataLen+2] = checkSum;

			//Send all of the values. The checkSum is used to verify that none of the values get messed up in transit.
			bool ok = radio.write(values, sizeof(values));

			// Now, continue listening
			radio.startListening();
		}

//End: mutual values.

unsigned long updateLastTime(int loopsPSec, unsigned long lastTime){
  int increment = 1000 / loopsPSec;  
  unsigned long nextTime = time + increment;
  if (nextTime <= time){
    return time;
  }
  return lastTime;
}

boolean trackTime(int loopsPSec, long lastTime){
  int increment = 1000 / loopsPSec;
  unsigned long nextTime = lastTime + increment;
  if (nextTime <= time){
    return true; 
  }
  return false;
}


void setup(void)
{
	//Initializes values.
  loopsPSec = 1;
  time = millis();
  lastTimeValue = time;
	mesStart = false;
  
  //Starts serial connection.
  Serial.begin(57600);
  while(!Serial);
  delay(2000);
  printf_begin();
  printf("\n\rBasic_Send\n\r");
  

	//Testing Only
	desOrient[0] = 1;
	desOrient[1] = 2;
	desOrient[2] = 3;
	desOrient[3] = 4;


  //Starts radio
  radio.begin();
  // optionally, reduce the payload size.  seems to
  // improve reliability
  //radio.setPayloadSize(8);
	radio.openWritingPipe(pipes[0]);
	radio.openReadingPipe(1,pipes[1]);
  radio.startListening();
  radio.printDetails();



}

void loop(void)
{
	//Updates current time.
  time = millis();

	if(trackTime(loopsPSec, lastTimeValue)){
    lastTimeValue = time;
    sendValues(desOrientStart, desOrient, desOrientLen);
		Serial.println("Sending!");
		//Testing
		desOrient[0] = desOrient[0] + 1;
		desOrient[1] += 2;
		desOrient[2] += 3;
		desOrient[3] += 4;
  }
	if (radio.available()){
		getValues();
		for(int i=0; i<curOrientLen; i++){
			Serial.print(String(curOrient[i]) + ", ");
		}
	Serial.println();
  }
	
}
