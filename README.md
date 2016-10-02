# apkr
[![Build status](https://ci.appveyor.com/api/projects/status/e1o3djbyvgo7m5u0?svg=true)](https://ci.appveyor.com/project/zerjioang/apkr)
[![Build Status](https://travis-ci.org/apkr/apkr.svg?branch=master)](https://travis-ci.org/apkr/apkr)
[![Coverage Status](https://coveralls.io/repos/github/apkr/apkr/badge.svg?branch=master)](https://coveralls.io/github/apkr/apkr?branch=master)


<p align="center">
<img src ="https://avatars1.githubusercontent.com/u/22367829?v=3&s=200" />
</p>

#What apkr is
<p align="justify">
<b>apkr</b> (originally named atom: <b>a</b>nalysis <b>t</b>hrough <b>o</b>bservation <b>m</b>achine)* is the codename for android apps/malware analysis/reversing tool. It was built focused on security issues and tricks that malware researcher have on they every day work. For those situations on where the malware has <b>anti-analysis</b> routines, apkr attemps to bypass them in order to get to the code and 'bad boy' routine. Sometimes those techniques can be virtual machine detection, emulator detection, self certificate checking, pipes detection. tracer pid check, and so on.

<b>apkr</b> uses an innovative idea in where the code is not decompiled rather than viewed. This allow us to get the global view of the execution workflow of the code with a 100% accuracy on gathered information. With this situation, <b>apkr</b> generates a fancy <b>html</b> report with the results for an easy understanding.
</p>

## Motivation

<p align="justify">
There is only one reason why someone would spend that kind of money to get malware delivered – because it will pay for itself. The article showed that one specific cybergang’s income from just one flavor of ransomware was almost $400,000 a month.
This shows a very dangerous combination of facts. Getting malware onto a victim’s computer is worth a lot of money, so people will pay handsomely for new exploits to make that happen. This makes exploits worth a lot of money, so people will be motivated to continue creating them.
</p>
> Source: http://lightpointsecurity.com/content/the-motivation-behind-malware

#Features

* .apk unpacker
* .apk file decoder
* .apk file enumeration
* .apk file classification and identification
* resource fuzzing and hashing
* native code dump
* signature certificate analysis
* debug certificate detection
* simple opcode analysis
* unused opcode detection
* androidManifest.xml analysis
* internal structure analysis
* dalvik bytecode flow analysis
* CFG generation
* simple reflection resolver
* String classification
* simulated workflow generation
* dynamic rules engine

## apkr modules

* PSCout data module
* Full Android manifest parser, based on official SDK documentation v23.
* Extensible API for third party plugins module
* Machine Learning (Weka based) module

## apkr plugins included

* Hidden plain ELF file detector
* Application UID detector
* Privacy plugin

# Installation

For installation purposes, it needs
  * Java SDK
  * Web browser
  * Internet Access.
  * Windows, MAC OS or Linux.
  * A Brain!
 
# Run
For deploying the server using default port (1234):
```
java -jar apkr.jar
```

# Contribution

To support this project you can:

  - Post thoughts about new features/optimizations that important to you
  - Submit bug using one of following ways:
    * Error stacktrace string and log files.
    * Error log and link to public available apk file.
  
And any other comments will be very appreciate.

#License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

