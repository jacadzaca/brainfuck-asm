


# About

A simple brainfuck to NASM assembly compiler, written in Clojure

##  Prerequisites
Make sure you have NASM assembler installed

## Creating an executable
```bash
    lein run INPUT_FILE > output.asm && nasm -g -f elf32 output.asm && ld -m elf_i386 -o runme output.o
<<<<<<< HEAD
```
#### Example
```bash
    lein run example.bf > output.asm && nasm -g -f elf32 output.asm && ld -m elf_i386 -o runme output.o
```
=======
``` 
Try compiling example.bf to try it out.
>>>>>>> f39cc8fef08cae08ffe0b7453f836a5dce97be4a
