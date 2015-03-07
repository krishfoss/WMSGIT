#include <avr/io.h>
#include <avr/delay.h>

#include "lcd.h"
#define F_CPU 8000000UL

//#define LCD_DATA B
//#define LDCD_DATA_POS 0
//
//#define LCD_E B
//#define LCD_E_POS PB4
//
//#define LCD_RS D
//#define LCD_RS_POS PD3
//
//#define LCD_RW D
//#define LCD_RW_POS PD6


void BlueInit(uint16_t ubrr_value) //This function is used to initialize the USART at a given UBRR value
{
 
   //Set Baud rate
 
   UBRRL = ubrr_value;
   UBRRH = (ubrr_value>>8);
   /*We Set Frame Format as
   >> Asynchronous mode
   >> No Parity
   >> 1 StopBit
   >> char size 8
   */
   UCSRC=(1<<URSEL)|(3<<UCSZ0);
   //Enable The RX receiver and TX transmitter
   UCSRB=(1<<RXEN)|(1<<TXEN);
 
}
 
char BlueRdChar() // function used to read data from USART line. It waits till any data is available
{
   
   while(!(UCSRA & (1<<RXC)))
   {
     
   }
 
   return UDR;
}
 
void BlueWrChar(char data) // function writes the character in 'data' into USART and then transmits it to PC via TX line
{
   
   while(!(UCSRA & (1<<UDRE)))
   {
      
   }
 
   UDR=data;
}
void Waiting(int j) // simple delay function
{
uint8_t i;
for(i=0;i<j;i++)
_delay_ms(200);
}
 
int main()
{
   char data;
   int i;
 
   /*First Initialize the USART with baud rate = 9600bps
   
   for Baud rate = 9600bps
 
   UBRR value = 103
   */
   BlueInit(51);    //UBRR = 103
   //Initialize LCD module
   LCDInit(LS_BLINK|LS_ULINE);
   //Clear the screen
   LCDClear();
    LCDWriteString("BLUETOOTH MODULE");
    LCDWriteStringXY(0,1,"  INTERFACING   ");
    Waiting(5);
    LCDClear();
    LCDWriteString("       By       ");
    LCDWriteStringXY(0,1,"RK");
    Waiting(5);
LCDClear();
 
   //Loop forever
   while(1)
   {
      LCDClear();
 LCDWriteString("Receiving Data.."); 
      for (i=0;i<=15;i++)
      {  
 data=BlueRdChar();
 
 BlueWrChar(data);
   
 LCDGotoXY(i,1);
 LCDData(data);
      }
 Waiting(2); 
 
   }
   return 0;
}
