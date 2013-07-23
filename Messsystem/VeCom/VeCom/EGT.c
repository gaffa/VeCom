#include <avr/io.h>
#include <util/delay.h>
#include "egt.h"


/**
 * initializes the ports and pins for egt measurement
 */
void egt_init(){
	
	//clk and cs as output
	EGT_REG |= (1 << EGT_REG_CLK) | (1 << EGT_REG_CS);
	//pullup for data pin
	EGT_PORT &= (1<<EGT_DATA);
	//cs high for measure
	EGT_PORT |= (1<<EGT_CS);
}

/**
 * communciates with max6675 to determine egt
 * all delay-calls are because the max6675 does not support timings as fast as the mega 
 */
uint16_t get_egt(){

	uint16_t temp=0;
	
	//cs low for data
	EGT_PORT &= ~(1<<EGT_CS); 
	_delay_ms(EGT_DELAY);

	//now go through relevant bits and shift them in
	for(int i=0; i<10; i++){

		//clk-toggle for next bit (skips irrelevant msb in first iteration)
		EGT_PORT |= (1 << EGT_CLK);
		_delay_ms(EGT_DELAY);
		EGT_PORT &= ~(1 << EGT_CLK);
		_delay_ms(EGT_DELAY);

		// shift
		temp = temp << 1;
		
		// add pin state (0 or 1)
		temp += EGT_PIN & (1<<EGT_DATA_PIN);		
	}
	
	//cs high for measure
	EGT_PORT |= (1<<EGT_CS); 

	return temp;
} 
