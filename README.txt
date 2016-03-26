Moby Dick 2

An Android app that uses Markov modeling in an asynctask to generate plausible word sequences based on an inputted corpus (by default, Moby Dick).

The input is read and tokenized, with 2 consecutive words (called a prefix) acting as the key with the next word acting as the value.  These pairs are stored in a hash table which sorts based on a rolling hash function.

The Markov modeling implementation is derived from The Practice of Programming by Kernighan and Pike.