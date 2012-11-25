#ifndef EGT_H_
#define EGT_H_

#define EGT_PORT PORTC
#define EGT_PIN PINC

#define EGT_DATA PINC2

#define EGT_CS PC1
#define EGT_CLK PC0

#define EGT_DELAY 1000

void egt_init();
uint16_t get_egt();

#endif
