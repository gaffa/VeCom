#ifndef EGT_H_
#define EGT_H_

#define EGT_PORT PORTC

#define EGT_PIN PINC

#define EGT_DATA_PIN PINC0

#define EGT_DATA PC0
#define EGT_CS PC1
#define EGT_CLK PC2

#define EGT_REG DDRC
#define EGT_REG_CLK DDC2
#define EGT_REG_CS DDC1

#define EGT_DELAY 1

void egt_init();
uint16_t get_egt();

#endif
