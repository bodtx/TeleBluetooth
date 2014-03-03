#include <SoftwareSerial.h>// import the serial library

SoftwareSerial Genotronex(2, 1); // RX, TX
int ledpin=4; // led on D13 will show blink on / off
char content[10] = "";
char character;
boolean ison=false;


void setup() {
  // put your setup code here, to run once:
  Genotronex.begin(9600);
  Genotronex.println("Bluetooth On please press 1 or 0 blink LED ..");
  pinMode(ledpin,OUTPUT);
}

void loop() {
  int i = 0;
  char content[10] = "";
  if (Genotronex.available() > 0) 
  {       
    while(Genotronex.available() > 0) {
      character = Genotronex.read();
      content[i] = character;
      i++;
    }
    content[i]='\0';
    if(strcmp(content,"on") == 0){  
      digitalWrite(ledpin,1);
      ison=true;
    }
    else if(strcmp(content,"state") == 0){
      if(ison)
        Genotronex.print(1);
      else
        Genotronex.print(0);
    }
    else
    {// if number 0 pressed ....
      digitalWrite(ledpin,0);
      ison=false;
    }

  }


  delay(100);// prepare for next data ...
}



