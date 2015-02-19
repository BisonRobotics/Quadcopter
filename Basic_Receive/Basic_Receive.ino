#include <nRF24L01.h>
#include <RF24.h>
#include <RF24_config.h>
#include <SPI.h>
#include <Wire.h>
#include "printf.h"
#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"

//Start: Quadcopter Specific Values.

	//Start: Gyro-Accel Module Values
		// class default I2C address is 0x68
		MPU6050 mpu;

		// MPU control/status vars
		bool dmpReady = false;  // set true if DMP init was successful
		uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
		uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
		uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
		uint16_t fifoCount;     // count of all bytes currently in FIFO
		uint8_t fifoBuffer[64]; // FIFO storage buffer

		// orientation/motion vars
		Quaternion q;           // [w, x, y, z]         quaternion container
		VectorFloat gravity;    // [x, y, z]            gravity vector
		float ypr[3];           // [yaw, pitch, roll]   yaw/pitch/roll container and gravity vector
	//End: Gyro-Accel
		
//End: Quadcopter Specific Values.



//Start: Mutual but different.

	const uint64_t pipes[2] = { 0xF0F0F0F0E1LL, 0xF0F0F0F0D2LL };

	//Initialize Radio
	RF24 radio(7,8);	//Currently an Arduino Micro pin 2 = CE, pin 3 = CSN

	//Indicator LEDs
	const int GLED = 5;
	const int RLED = 6;

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

		/* Actual Orientation converted into ints		TODO -8: GyTime.
		-0: height
		-1: roll
		-2: pitch
		-3: yaw
		*/
		int curOrient[curOrientLen];
		
		/*Actual Orientation
		-0: height
		-1: roll
		-2: pitch
		-3: yaw
		*/
		float curDecOrient[curOrientLen];
		
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
			
			//In the future the loop will be used for multiple packets.
			bool done = false;
			while(!done){
				done = radio.read(values, sizeof(values));
			}
			
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
					//Convert Values to int for efficient transport and send.
					convertDecOrientToInt();
					sendValues(curOrientStart, curOrient, curOrientLen);
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

			if (ok)
					Serial.println("sent");
			else
				printf("failed.\n\r");
				
			// Now, continue listening
			radio.startListening();
		}

//End: mutual values.

void convertDecOrientToInt(){
	for(int i=0; i<curOrientLen; i++){
			curOrient[i] = (int)(curDecOrient[i] * 100);
		}
}

void getGyro(){
		// if programming failed, don't try to do anything
    if (!dmpReady) return;

    // reset interrupt flag and get INT_STATUS byte
    mpuIntStatus = mpu.getIntStatus();

    // get current FIFO count
    fifoCount = mpu.getFIFOCount();

    // check for overflow (this should never happen unless our code is too inefficient)
    if ((mpuIntStatus & 0x10) || fifoCount == 1024) {
        // reset so we can continue cleanly
        mpu.resetFIFO();
        Serial.println(F("FIFO overflow!"));

    // otherwise, check for DMP data ready interrupt (this should happen frequently)
    } else if (mpuIntStatus & 0x02) {
        // wait for correct available data length, should be a VERY short wait
        while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();

        // read a packet from FIFO
        mpu.getFIFOBytes(fifoBuffer, packetSize);
        
        // track FIFO count here in case there is > 1 packet available
        // (this lets us immediately read more without waiting for an interrupt)
        fifoCount -= packetSize;

				mpu.dmpGetQuaternion(&q, fifoBuffer);
				mpu.dmpGetGravity(&gravity, &q);
				mpu.dmpGetYawPitchRoll(ypr, &q, &gravity);
				curDecOrient[3] = (ypr[0] * 180/M_PI);
				curDecOrient[2] = (ypr[1] * 180/M_PI);
				curDecOrient[1] = (ypr[2] * 180/M_PI);
				
    }
	//TODO figure out how to send a long time.
}

void initGyro(){
	// join I2C bus (I2Cdev library doesn't do this automatically)
	Wire.begin();
	TWBR = 24; // 400kHz I2C clock (200kHz if CPU is 8MHz)

	// initialize device
	Serial.println(F("Initializing I2C devices..."));
	mpu.initialize();

	// load and configure the DMP
	Serial.println(F("Initializing DMP..."));
	devStatus = mpu.dmpInitialize();

	// supply your own gyro offsets here, scaled for min sensitivity
	mpu.setXGyroOffset(220);
	mpu.setYGyroOffset(76);
	mpu.setZGyroOffset(-85);
	mpu.setZAccelOffset(1788); // 1688 factory default for my test chip

	// make sure it worked (returns 0 if so)
	if (devStatus == 0) {
		// turn on the DMP, now that it's ready
		Serial.println(F("Enabling DMP..."));
		mpu.setDMPEnabled(true);

		// set our DMP Ready flag so the main loop() function knows it's okay to use it
		Serial.println(F("DMP ready! Waiting for first interrupt..."));
		dmpReady = true;

		// get expected DMP packet size for later comparison
		packetSize = mpu.dmpGetFIFOPacketSize();
	} else {
		// ERROR!
		// 1 = initial memory load failed
		// 2 = DMP configuration updates failed
		// (if it's going to break, usually the code will be 1)
		Serial.print(F("DMP Initialization failed (code "));
		Serial.print(devStatus);
		Serial.println(F(")"));
	}
}

void setup(void)
{
	//Initialize the values.
  time = millis();
  lastTimeValue = time;
  mesStart = false;

  //Initialize indicator LEDs.
  pinMode(RLED, OUTPUT);
  pinMode(GLED, OUTPUT);
  
	//Gyroscope
  initGyro();
	getGyro();
	
	//Initialize serial connection.
  Serial.begin(57600);
  while(!Serial);
  delay(2000);
  printf_begin();
  printf("\n\rBasic_Receive\n\r");

	//Starts radio pipe.
  radio.begin();
  // optionally, reduce the payload size.  seems to
  // improve reliability
  //radio.setPayloadSize(8);
	radio.openWritingPipe(pipes[1]);
	radio.openReadingPipe(1,pipes[0]);
  radio.startListening();
  radio.printDetails();
		
	//Testing Only
	incVal = 0;
}

void loop(void)
{
	//Updates current time.
	time = millis();

	//Get's Gyro Values
	getGyro();
	
	//Checks for values from controller.
  if (radio.available()){
		ledOnOff(GLED, HIGH);
		getValues();
		for(int i=0; i<desOrientLen; i++){
			Serial.print(String(desOrient[i]) + ", ");
		}
		Serial.println();
		
		
  } else {
    ledOnOff(GLED, LOW);
  }
}













	/* Gyro Code
	Wire.beginTransmission(MPU);
  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU,14,true);  // request a total of 14 registers
  AcX=Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)    
  AcY=Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  AcZ=Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)
  Tmp=Wire.read()<<8|Wire.read();  // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)
  GyX=Wire.read()<<8|Wire.read();  // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
  GyY=Wire.read()<<8|Wire.read();  // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  GyZ=Wire.read()<<8|Wire.read();  // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)
  //Serial.print(" | Tmp = "); Serial.print(Tmp/340.00+36.53);  //equation for temperature in degrees C from datasheet
	
	//Update Gyro Values on curOrient
	curOrient[0] = AcX;
	curOrient[1] = AcY;
	curOrient[2] = AcZ;
	curOrient[3] = Tmp;
	curOrient[4] = GyX;
	curOrient[5] = GyY;
	curOrient[6] = GyZ;
	*/



