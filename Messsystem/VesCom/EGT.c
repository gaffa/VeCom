#include <avr/io.h>
#include <util/delay.h>
#include "egt.h"

#define EGT_REGISTER DDRA


/**
 * initializes the ports and pins for egt measurement
 */
void egt_init(){
	//datenrichtung
	DDRC |= (1 << DDC0) | (1 << DDC1);

	//cs high for measure
	EGT_PORT |= (1 << EGT_CS);

	//clk low
	EGT_PORT &= ~(1 << EGT_CLK);
}

/**
 * communciates with max6675 to determine egt
 * all delay-calls are because the max6675 does not support timings as fast as the mega 
 */
uint16_t get_egt(){
	uint16_t temp=0;

	//cs low for data
	EGT_PORT &= ~(1 << EGT_CS); 
	_delay_ms(EGT_DELAY);

	//clk high for irrelevant bit
	EGT_PORT |= (1 << EGT_CLK); 
	_delay_ms(EGT_DELAY);

	//clk low
	EGT_PORT &= ~(1 << EGT_CLK); 
	_delay_ms(EGT_DELAY);

	//now go through relevant bits and shift them in
	for(int i=0; i<10; i++){
		//clk high for next bit
		EGT_PORT |= (1 << EGT_CLK); 
		_delay_ms(EGT_DELAY);

		//shift in bit 
		temp = temp << (EGT_PIN & (1 << EGT_DATA)); 
		
		//clk low
		EGT_PORT &= ~(1 << EGT_CLK); 
		_delay_ms(EGT_DELAY);
	}

	return temp;
} 
