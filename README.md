# Odd or even, the dumb way to learn things
This repo is inspired by [4 billon if statements](https://andreasjhkarlsson.github.io/jekyll/update/2023/12/27/4-billion-if-statements.html) and explores the max number of conditions that you can process in a single java file

# Sorry
This is not production code and thus was written with my feet.

# How to use
You need Java 21 with preview features.
Edit Generator.java with your prefered generation stategy.
```
# Generate OddOrEven.java with 1000 conditions
java --source 21 --enable-preview Generator.java 1000 

# Run this incredible generated code
java --source 21 --enable-preview OddOrEven.java 98 
```

