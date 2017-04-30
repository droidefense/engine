# Droidefense
[![Build status](https://ci.appveyor.com/api/projects/status/e1o3djbyvgo7m5u0?svg=true)](https://ci.appveyor.com/project/droidefense/engine)
[![Build Status](https://travis-ci.org/Droidefense/engine.svg?branch=master)](https://travis-ci.org/droidefense/engine)
[![Coverage Status](https://coveralls.io/repos/github/Droidefense/engine/badge.svg?branch=master)](https://coveralls.io/github/droidefense/engine?branch=master)
[![Join the chat at https://gitter.im/Droidefense/Lobby](https://badges.gitter.im/Droidefense/engine.svg)](https://gitter.im/Droidefense/Lobby)
[![License](http://img.shields.io/:license-gpl3-blue.svg)](https://raw.githubusercontent.com/Droidefense/engine/master/LICENSE)

## First release coming soon

<p align="center">
<img src ="https://raw.githubusercontent.com/droidefense/engine/develop/banner/report-template.png" />
</p>

# What Droidefense is
<p align="justify">
<b>Droidefense</b> (originally named atom: <b>a</b>nalysis <b>t</b>hrough <b>o</b>bservation <b>m</b>achine)* is the codename for android apps/malware analysis/reversing tool. It was built focused on security issues and tricks that malware researcher have on they every day work. For those situations on where the malware has <b>anti-analysis</b> routines, Droidefense attemps to bypass them in order to get to the code and 'bad boy' routine. Sometimes those techniques can be virtual machine detection, emulator detection, self certificate checking, pipes detection. tracer pid check, and so on.

<b>Droidefense</b> uses an innovative idea in where the code is not decompiled rather than viewed. This allow us to get the global view of the execution workflow of the code with a 100% accuracy on gathered information. With this situation, <b>Droidefense</b> generates a fancy <b>html</b> report with the results for an easy understanding.
</p>

# Usage

## TL;DR

```
java -jar droidefense.jar -i /path/to/your/sample.apk
```

# Contribution

To support this project you can:

  - Post thoughts about new features/optimizations that important to you
  - Submit bug using one of following ways:
    * Error stacktrace string and log files.
    * Error log and link to public available apk file.
  - Do NOT forget to fullfil [issue template](https://raw.githubusercontent.com/droidefense/engine/master/.github/ISSUE_TEMPLATE.md)
  
And any other comments will be very appreciate.

#License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

