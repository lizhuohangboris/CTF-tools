# Self-Modified Code

## 简介

**自修改代码**（Self-Modified Code）是一类特殊的代码技术，即**在运行时修改自身代码**，从而使得程序实际行为与反汇编结果不符，同时修改前的代码段数据也可能非合法指令，从而无法被反汇编器识别，这加大了软件逆向工程的难度。

自修改代码通常有两种破解方式，第一种是根据静态分析结果直接修改程序二进制文件，第二种则是在动态调试时将解密后的程序从内存中 dump 下来。

## 例题：GWCTF 2019 - re3

惯例拖入 IDA，主函数逻辑比较简单，主要是利用 `mprotect()` 修改代码段权限后再改写 `sub_402219()` 开头的 234 字节数据，最后再调用 `sub_40207B()` 与 `sub_402219()`：

```c
void __fastcall __noreturn main(int a1, char **a2, char **a3)
{
  int i; // [rsp+8h] [rbp-48h]
  char s[40]; // [rsp+20h] [rbp-30h] BYREF
  unsigned __int64 v5; // [rsp+48h] [rbp-8h]

  v5 = __readfsqword(0x28u);
  __isoc99_scanf("%39s", s);
  if ( (unsigned int)strlen(s) != 32 )
  {
    puts("Wrong!");
    exit(0);
  }
  mprotect(&dword_400000, 0xF000uLL, 7);
  for ( i = 0; i <= 223; ++i )
    *((_BYTE *)sub_402219 + i) ^= 0x99u;
  sub_40207B(&unk_603170);
  sub_402219(s);
}
```

此时我们可以看到 `sub_402219()` 处数据全都无法被识别：

```c
.text:0000000000402219 sub_402219      proc near               ; CODE XREF: main+CA↑p
.text:0000000000402219                                         ; DATA XREF: main:loc_40217C↑o
.text:0000000000402219 ; __unwind {
.text:0000000000402219                 int     3               ; Trap to Debugger
.text:0000000000402219 sub_402219      endp
.text:0000000000402219
.text:0000000000402219 ; ---------------------------------------------------------------------------
.text:000000000040221A                 dw 10D1h, 0D17Ch, 7518h
.text:0000000000402220                 dq 812410D199999969h, 0BC9D12D1FD666666h, 61DC10D1999999B1h
.text:0000000000402220                 dq 6666A91C14D159A8h, 10D199F9A8E92766h, 12D1666671BA715Eh
.text:0000000000402220                 dq 1C14D1666666810Ch, 0D14F10D1666666A9h, 0D166666E9E715E10h
.text:0000000000402220                 dq 14D1666666811C12h, 6666A91C14D189C9h, 715E10D14F10D166h
.text:0000000000402220                 dq 66B11C5E66666F73h, 1C5E999999986666h, 99999999666666B5h
.text:0000000000402220                 dq 666666B51C12A372h, 66811C12D149FAD1h, 892F964998D16666h
.text:0000000000402220                 dq 1D1666666B51C12h, 0A199F9A939192F96h, 6666B11C5E93ED5Bh
.text:0000000000402220                 dq 0B51C1A9999999966h, 66B5241A98666666h, 0B11C1224E7866666h
.text:0000000000402220                 dq 0FD61D412D1666666h, 999999B1BC95AAD1h, 5066667AC0719CEDh
.text:0000000000402220                 dq 801F0F5Ah
```

按照 `main()` 逻辑将这块区域进行修改：

```python
import idc

for i in range(234):
    idc.patch_byte(0x402219 + i, idc.get_db_byte(0x402219 + i) ^ 0x99)
```

这个时候我们便能获得一个正常的函数了：

```c
.text:0000000000402219 sub_402219      proc near               ; CODE XREF: main+CA↑p
.text:0000000000402219                                         ; DATA XREF: main:loc_40217C↑o
.text:0000000000402219 ; __unwind {
.text:0000000000402219                 push    rbp             ; Trap to Debugger
.text:0000000000402219 sub_402219      endp
.text:0000000000402219
.text:000000000040221A ; ---------------------------------------------------------------------------
.text:000000000040221A                 mov     rbp, rsp
.text:000000000040221D                 sub     rsp, 0F0h
.text:0000000000402224                 mov     [rbp-0E8h], rdi
.text:000000000040222B                 mov     rax, fs:28h
.text:0000000000402234                 mov     [rbp-8], rax
.text:0000000000402238                 xor     eax, eax
.text:000000000040223A                 lea     rax, [rbp-0D0h]
.text:0000000000402241                 mov     esi, offset unk_603170
```

在 `sub_402219()` 开头按 `alt + p` 重新定义函数范围（也可以先按 `u` 取消原有定义后再按 `p` 重建定义），此时我们便能获得正确的反编译结果：

```c
// positive sp value has been detected, the output may be wrong!
__int64 __fastcall sub_402219(__int64 a1)
{
  unsigned int v2; // [rsp+18h] [rbp-D8h]
  int i; // [rsp+1Ch] [rbp-D4h]
  char v4[200]; // [rsp+20h] [rbp-D0h] BYREF
  unsigned __int64 v5; // [rsp+E8h] [rbp-8h]

  v5 = __readfsqword(0x28u);
  sub_400A71(v4, &unk_603170);
  sub_40196E(v4, a1);
  sub_40196E(v4, a1 + 16);
  v2 = 1;
  for ( i = 0; i <= 31; ++i )
  {
    if ( *(_BYTE *)(i + a1) != byte_6030A0[i] )
      v2 = 0;
  }
  return v2;
}
```

`sub_402219()` 首先会调用 `sub_400A71()`，最终会调用到 `sub_4007C6()` ，主要作用就是把 `0x603170` 处的 16 字节拷贝到栈上，这里就不展开了。

接下来会调用两次 `sub_40196E()` ，第一个参数为拷贝到栈上的 `0x603170` 处的 16 字节，第二个参数分别为我们的输入 `&s[0]` 及 `&s[16]`。该函数最后会调用到 `sub_401828()`，非常明显的 AES 加密特征: 初始变换→9轮循环运算→最终运算，调用的几个函数也符合 AES 内部逻辑，这里就不展开了：

```c
__int64 __fastcall sub_401828(__int64 a1, __int64 a2)
{
  unsigned __int8 i; // [rsp+1Fh] [rbp-1h]

  sub_400B0A(0LL, a1, a2);
  for ( i = 1; i <= 9u; ++i )
  {
    sub_400BAC(a1);
    sub_400C1F(a1);
    sub_400D27(a1);
    sub_400B0A(i, a1, a2);
  }
  sub_400BAC(a1);
  sub_400C1F(a1);
  return sub_400B0A(10LL, a1, a2);
}
```

在 `sub_402219()` 中最后会将加密后的输入与 `0x6030A0` 处数据进行对比：

```c
.data:00000000006030A0 ; char byte_6030A0[32]
.data:00000000006030A0 byte_6030A0     db 0BCh                 ; DATA XREF: sub_402219+9F↑r
.data:00000000006030A1                 db  0Ah
.data:00000000006030A2                 db 0ADh
.data:00000000006030A3                 db 0C0h
.data:00000000006030A4                 db  14h
.data:00000000006030A5                 db  7Ch ; |
.data:00000000006030A6                 db  5Eh ; ^
.data:00000000006030A7                 db 0CCh
.data:00000000006030A8                 db 0E0h
.data:00000000006030A9                 db 0B1h
.data:00000000006030AA                 db  40h ; @
.data:00000000006030AB                 db 0BCh
.data:00000000006030AC                 db  9Ch
.data:00000000006030AD                 db  51h ; Q
.data:00000000006030AE                 db 0D5h
.data:00000000006030AF                 db  2Bh ; +
.data:00000000006030B0                 db  46h ; F
.data:00000000006030B1                 db 0B2h
.data:00000000006030B2                 db 0B9h
.data:00000000006030B3                 db  43h ; C
.data:00000000006030B4                 db  4Dh ; M
.data:00000000006030B5                 db 0E5h
.data:00000000006030B6                 db  32h ; 2
.data:00000000006030B7                 db  4Bh ; K
.data:00000000006030B8                 db 0ADh
.data:00000000006030B9                 db  7Fh ; 
.data:00000000006030BA                 db 0B4h
.data:00000000006030BB                 db 0B3h
.data:00000000006030BC                 db  9Ch
.data:00000000006030BD                 db 0DBh
.data:00000000006030BE                 db  4Bh ; K
.data:00000000006030BF                 db  5Bh ; [
.data:00000000006030C0 unk_6030C0      db    3                 ; DATA XREF: sub_40207B+5B↑o
.data:00000000006030C1                 db    5
.data:00000000006030C2                 db    7
.data:00000000006030C3                 db  0Bh
.data:00000000006030C4                 db  0Dh
.data:00000000006030C5                 db  11h
.data:00000000006030C6                 db  13h
.data:00000000006030C7                 db  17h
.data:00000000006030C8                 db  1Dh
.data:00000000006030C9                 db  1Fh
.data:00000000006030CA                 db  25h ; %
.data:00000000006030CB                 db  29h ; )
.data:00000000006030CC                 db  2Bh ; +
.data:00000000006030CD                 db  2Fh ; /
.data:00000000006030CE                 db  35h ; 5
.data:00000000006030CF                 db  3Bh ; ;
.data:00000000006030D0                 db  3Dh ; =
.data:00000000006030D1                 db  43h ; C
.data:00000000006030D2                 db  47h ; G
.data:00000000006030D3                 db  49h ; I
.data:00000000006030D4                 db  4Fh ; O
.data:00000000006030D5                 db  53h ; S
.data:00000000006030D6                 db  59h ; Y
.data:00000000006030D7                 db  61h ; a
.data:00000000006030D8                 db  65h ; e
.data:00000000006030D9                 db  67h ; g
.data:00000000006030DA                 db  6Bh ; k
.data:00000000006030DB                 db  6Dh ; m
.data:00000000006030DC                 db  71h ; q
.data:00000000006030DD                 db  7Fh ; 
.data:00000000006030DE                 db  83h
.data:00000000006030DF                 db  89h
.data:00000000006030E0                 db  8Bh
.data:00000000006030E1                 db  95h
.data:00000000006030E2                 db  97h
.data:00000000006030E3                 db  9Dh
.data:00000000006030E4                 db 0A3h
.data:00000000006030E5                 db 0A7h
.data:00000000006030E6                 db 0ADh
.data:00000000006030E7                 db 0B3h
.data:00000000006030E8                 db 0B5h
.data:00000000006030E9                 db 0BFh
.data:00000000006030EA                 db 0C1h
.data:00000000006030EB                 db 0C5h
.data:00000000006030EC                 db 0C7h
.data:00000000006030ED                 db 0D3h
.data:00000000006030EE                 db 0DFh
.data:00000000006030EF                 db 0E3h
.data:00000000006030F0                 db 0E5h
.data:00000000006030F1                 db 0E9h
.data:00000000006030F2                 db 0EFh
.data:00000000006030F3                 db 0F1h
.data:00000000006030F4                 db 0FBh
```

那么加密密钥 `0x603170` 上是什么数据呢？我们回到主函数，在调用 `sub_402219()` 之前首先会调用 `sub_40207B()`，其主要逻辑是调用了好几次 `sub_401CF9()`， 传入的参数都是位于 `data` 段上的数据：

```c
unsigned __int64 __fastcall sub_40207B(__int64 a1)
{
  char v2[16]; // [rsp+10h] [rbp-50h] BYREF
  __int64 v3; // [rsp+20h] [rbp-40h] BYREF
  __int64 v4; // [rsp+30h] [rbp-30h] BYREF
  __int64 v5; // [rsp+40h] [rbp-20h] BYREF
  unsigned __int64 v6; // [rsp+58h] [rbp-8h]

  v6 = __readfsqword(0x28u);
  sub_401CF9(&unk_603120, 64LL, v2);
  sub_401CF9(&unk_603100, 20LL, &v3);
  sub_401CF9(&unk_6030C0, 53LL, &v4);
  sub_401CF9(&dword_4025C0, 256LL, &v5);
  sub_401CF9(v2, 64LL, a1);
  return __readfsqword(0x28u) ^ v6;
}
```

而看到 `sub_401CF9()` 开头的四个常数就知道这应该是 `md5` 算法，仔细一看程序逻辑也是如此：

```c
unsigned __int64 __fastcall sub_401CF9(const void *a1, size_t a2, __int64 a3)
{
  unsigned int v5; // [rsp+28h] [rbp-98h]
  unsigned int v6; // [rsp+2Ch] [rbp-94h]
  unsigned int v7; // [rsp+30h] [rbp-90h]
  unsigned int v8; // [rsp+34h] [rbp-8Ch]
  unsigned int v9; // [rsp+38h] [rbp-88h]
  unsigned int v10; // [rsp+3Ch] [rbp-84h]
  unsigned int v11; // [rsp+40h] [rbp-80h]
  unsigned int v12; // [rsp+44h] [rbp-7Ch]
  unsigned int l; // [rsp+48h] [rbp-78h]
  unsigned int m; // [rsp+48h] [rbp-78h]
  int v15; // [rsp+4Ch] [rbp-74h]
  int v16; // [rsp+50h] [rbp-70h]
  unsigned int v17; // [rsp+54h] [rbp-6Ch]
  size_t i; // [rsp+58h] [rbp-68h]
  size_t j; // [rsp+60h] [rbp-60h]
  size_t k; // [rsp+60h] [rbp-60h]
  char *dest; // [rsp+68h] [rbp-58h]
  int v22[18]; // [rsp+70h] [rbp-50h]
  unsigned __int64 v23; // [rsp+B8h] [rbp-8h]

  v23 = __readfsqword(0x28u);
  v5 = 0x67452301;
  v6 = 0xEFCDAB89;
  v7 = 0x98BADCFE;
  v8 = 0x10325476;
  for ( i = a2 + 1; (i & 0x3F) != 56; ++i )
    ;
  dest = (char *)malloc(i + 8);
  memcpy(dest, a1, a2);
  dest[a2] = 0x80;
  for ( j = a2 + 1; j < i; ++j )
    dest[j] = 0;
  sub_401C63((unsigned int)(8 * a2), &dest[i]);
  sub_401C63((unsigned int)(a2 >> 29), &dest[i + 4]);
  for ( k = 0LL; k < i; k += 64LL )
  {
    for ( l = 0; l <= 0xF; ++l )
      v22[l] = sub_401CAC(&dest[4 * l + k]);
    v9 = v5;
    v10 = v6;
    v11 = v7;
    v12 = v8;
    for ( m = 0; m <= 0x3F; ++m )
    {
      if ( m > 0xF )
      {
        if ( m > 0x1F )
        {
          if ( m > 0x2F )
          {
            v15 = v11 ^ (v10 | ~v12);
            v16 = (7 * (_BYTE)m) & 0xF;
          }
          else
          {
            v15 = v12 ^ v11 ^ v10;
            v16 = (3 * (_BYTE)m + 5) & 0xF;
          }
        }
        else
        {
          v15 = v10 & v12 | v11 & ~v12;
          v16 = (5 * (_BYTE)m + 1) & 0xF;
        }
      }
      else
      {
        v15 = v11 & v10 | v12 & ~v10;
        v16 = m;
      }
      v17 = v12;
      v12 = v11;
      v11 = v10;
      v10 += __ROL4__(v22[v16] + dword_4025C0[m] + v15 + v9, dword_4026C0[m]);
      v9 = v17;
    }
    v5 += v9;
    v6 += v10;
    v7 += v11;
    v8 += v12;
  }
  free(dest);
  sub_401C63(v5, a3);     // 数据拷贝，这里就不展开了
  sub_401C63(v6, a3 + 4);
  sub_401C63(v7, a3 + 8);
  sub_401C63(v8, a3 + 12);
  return __readfsqword(0x28u) ^ v23;
}
```

因此 `sub_402219()` 的逻辑便清晰了：进行多轮 `md5` 哈希并将结果存储到 `0x603170` 处，我们直接动态调试一下获得此处数据：

```shell
pwndbg> x /16bx 0x603170
0x603170:	0xcb	0x8d	0x49	0x35	0x21	0xb4	0x7a	0x4c
0x603178:	0xc1	0xae	0x7e	0x62	0x22	0x92	0x66	0xce
```

AES 是对称加密算法，现在我们有了密钥（`0x603170`），又有了密文（`0x6030A0`），直接用 python 内置的密码学库即可获得 flag：

```python
from Crypto.Cipher import AES
from Crypto.Util.number import *

key = bytes.fromhex('cb8d493521b47a4cc1ae7e62229266ce')
cipher_text = bytes.fromhex('BC0AADC0147C5ECCE0B140BC9C51D52B46B2B9434DE5324BAD7FB4B39CDB4B5B')
plain_text = AES.new(key, AES.MODE_ECB).decrypt(cipher_text)
print(plain_text)
# flag{924a9ab2163d390410d0a1f670}
```
