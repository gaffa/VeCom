#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <string.h>
#include <stdlib.h>

#include "SerialCommunication.h"

#define FOSC 8000000// Clock Speed
#define BAUD 19200 //gps
#define MYUBRR (FOSC/16/BAUD-1)

int packetNumber = 0;

/**
 * increases packet number and resets it when it would soon overflow otherwise
 */
int getPacketNumber(){
	if(packetNumber>60000){
		packetNumber = 0;
	}
	packetNumber++;
	return packetNumber;
}

/**
 * initializes the uart
 */
void uart_init(){
	UCSR0B = (1<<RXCIE0)|(1<<RXEN0)|(1<<TXEN0); 
	UCSR0C |= (3<<UCSZ00);    // asynchron 8N1 

	UBRR0H = (unsigned char)(MYUBRR>>8);
	UBRR0L = (unsigned char)MYUBRR;
}

/**
 * sends a single character via uart
 */
void uart_putc(unsigned char c)
{
	// wait for uart to be ready
    while (!(UCSR0A & (1<<UDRE0))){
    }                             
 
	// put character in output register
    UDR0 = c;                     
	
	// wait for uart to be ready again (means character was sent)
	while (!(UCSR0A & (1<<UDRE0))){
    }   
}

/**
 * sends a character array via uart
 */
void uart_puts (char *s)
{
    while (*s)
    {   
		// simply putc for each character in the array/string
		uart_putc(*s);
        s++;
    }
}

/**
 * adds a string representation of a given integer to a string at the given offset
 * make sure that the given integer is not longer than 10 digits at a base of 10
 * and that the string that we append to has enough reserved memory
 * returns the number of digits added
 */
int addIntToString(char* existingString, int offset, int addMe){
	
	// stringbuffer
	char stringbuffer[10];

	// convert to string
	itoa(addMe, stringbuffer, 10);
	
	// iterator as pointer to the currently handled digit
	int iterator = 0;
	while(stringbuffer[iterator] != '\0'){
		// set digit to existing string
		existingString[iterator + offset] = stringbuffer[iterator];
		// increase iterator
		iterator++;
	}

	// as the iterator get increased after each successful digit and also before end of string
	// it already contains the exact number of digits added
	return iterator;
}

/**
 * sends a vescompacket via uart from given values
 */
void sendPacket(int rpm_diff, int v_diff, int egt){
	char message[29];
	int pointer = 0;
	
	message[pointer] = '*';
	pointer++;

	pointer += addIntToString(message, pointer, getPacketNumber());
	
	message[pointer] = ',';
	pointer++;

	pointer += addIntToString(message, pointer, rpm_diff);
	
	message[pointer] = ',';
	pointer++;

	pointer += addIntToString(message, pointer, v_diff);

	message[pointer] = ',';
	pointer++;

	pointer += addIntToString(message, pointer, egt);

	message[pointer] = '#';
	message[pointer+1] = '\0';

	uart_puts(message);
}
