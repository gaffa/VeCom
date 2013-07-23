#define F_CPU 8000000UL

#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <avr/eeprom.h>
#include <string.h>
#include <avr/wdt.h>
#include <stdlib.h>

#include "SerialCommunication.h"
#include "EGT.h"

// clock prescaler
#define CLOCK_PRESCALER 256
// resulting ticks per second of clock
#define TICKS_PER_SECOND F_CPU/CLOCK_PRESCALER
// resulting ticks per quarter second
#define TICKS_PER_QUARTER_SECOND TICKS_PER_SECOND/4

//the old timestamp of rpm
volatile uint16_t rpm_old=0;
//the timediff
volatile uint16_t rpm_diff=0;


//time of last tacho impulse 
volatile uint16_t v_old=0;
//v timediff
volatile uint16_t v_diff=0;

/**
 * tacho interrupt-routine
 */
ISR(INT1_vect){
	//toggle test pin
	PINB |= ( 1 << PB0 );

	if(v_old == 0){
		v_diff = 0;
	}else{
		v_diff = TCNT1 - v_old;
	}
	v_old = TCNT1;
}

/**
 * analog compare interrupt
 */
ISR(ANALOG_COMP_vect){	
	//toggle test pin
	PINB |= ( 1 << PB1 );
	
	if(rpm_old == 0){
		rpm_diff = 0;
	}else{
		rpm_diff = TCNT1 - rpm_old;
	}
	rpm_old = TCNT1;
}

/**
 * program-entry with main loop
 */ 
int main(void){
	
	// set timer1 (system clock) prescaler to 256
	TCCR1B |= (1<<CS12);

	// set interrupt-pin interrupts for rpm/v
	// v, rising edge
	EICRA |= (1<<ISC11)| (1<<ISC10);
	EIMSK |= (1<<INT1);
	
	// initialize uart
	uart_init();

	// initialize egt
	egt_init();

	//enable analog compare interrupt for rpm
	ACSR |= (1 << ACIS1) | (1 << ACIS0);
	ACSR |= (1 << ACIE);

	// enable interrupts
	sei();

	// last packet send time
	int lastPacketSendTime = 0;

	// last egt measure time
	int lastEGTMeasureTime = 0;
	// last egt measurements value
	int egtValue = 0;
	
	while(1){
		// send a packet every quarter second
		if(lastPacketSendTime == 0 || TCNT1 - lastPacketSendTime >= TICKS_PER_QUARTER_SECOND){
			sendPacket(rpm_diff, v_diff, egtValue);
			lastPacketSendTime = TCNT1;
		}
		
		// set rpm diff to 0 if no signal for more than a quarter second
		if(TCNT1 - rpm_old > TICKS_PER_QUARTER_SECOND){
			rpm_diff = 0;
			rpm_old = 0;
		}
		
		// set v diff to 0 if no signal for more than a second
		if(TCNT1 - v_old > TICKS_PER_SECOND){
			v_diff = 0;
			v_old = 0;
		}

		// measure egt every second
		if(lastEGTMeasureTime == 0 || TCNT1 - lastEGTMeasureTime >= TICKS_PER_SECOND){
			egtValue = get_egt();
			lastEGTMeasureTime = TCNT1;
		}
	}
}
