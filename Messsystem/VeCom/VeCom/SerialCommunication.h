void uart_init();
void uart_putc(unsigned char c);
void uart_puts (char *s);
void sendPacket(int rpm_diff, int v_diff, int egt);
int addIntToString(char* existingString, int offset, int addMe);
