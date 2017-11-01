# Droidefense
[![Build Status](https://travis-ci.org/droidefense/engine.svg?branch=develop)](https://travis-ci.org/droidefense/engine)
[![License](http://img.shields.io/:license-gpl3-blue.svg)](https://raw.githubusercontent.com/Droidefense/engine/master/LICENSE)

## First release coming soon

<p align="center">
<img src ="https://raw.githubusercontent.com/droidefense/engine/develop/banner/banner.png" />
</p>

# What Droidefense is
<p align="justify">
<b>Droidefense</b> (originally named atom: <b>a</b>nalysis <b>t</b>hrough <b>o</b>bservation <b>m</b>achine)* is the codename for android apps/malware analysis/reversing tool. It was built focused on security issues and tricks that malware researcher have on they every day work. For those situations on where the malware has <b>anti-analysis</b> routines, Droidefense attemps to bypass them in order to get to the code and 'bad boy' routine. Sometimes those techniques can be virtual machine detection, emulator detection, self certificate checking, pipes detection. tracer pid check, and so on.

<b>Droidefense</b> uses an innovative idea in where the code is not decompiled rather than viewed. This allow us to get the global view of the execution workflow of the code with a 100% accuracy on gathered information. With this situation, <b>Droidefense</b> generates a fancy <b>html</b> report with the results for an easy understanding.
</p>

# Usage

## TL;DR

```
java -jar droidefense-cli-1.0-SNAPSHOT.jar -i /path/to/your/sample.apk
```

# Citing

Feel free to cite droidefense on your works. We added next boilerplate for your references:

```
@Manual{,
  title        = {Droidefense: Advance Android Malware Analysis Framework},
  author       = {{zerjioang}},
  organization = {opensource},
  address      = {Bilbao, Spain},
  year         = 2017,
  url          = {https://droidefense.wordpress.com/}
}
```

# Contribution

To support this project you can:

  - Post thoughts about new features/optimizations that important to you
  - Submit bug using one of following ways:
    * Error stacktrace string and log files.
    * Error log and link to public available apk file.
  - Do NOT forget to fullfil [issue template](https://raw.githubusercontent.com/droidefense/engine/master/.github/ISSUE_TEMPLATE.md)
  
And any other comments will be very appreciate.

# License

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Uses GPL license described below

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
