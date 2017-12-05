# Droidefense

<p align="center">
</br>
<img src ="https://avatars3.githubusercontent.com/u/22367829?s=200&v=4" />
</p>

[![Build Status](https://travis-ci.org/droidefense/engine.svg?branch=develop)](https://travis-ci.org/droidefense/engine)
[![License](http://img.shields.io/:license-gpl3-blue.svg)](https://raw.githubusercontent.com/Droidefense/engine/develop/LICENSE)

## Latest release

[**Download**](https://github.com/droidefense/engine/releases/)

## What Droidefense is

<p align="justify">
<b>Droidefense</b> (originally named atom: <b>a</b>nalysis <b>t</b>hrough <b>o</b>bservation <b>m</b>achine)* is the codename for android apps/malware analysis/reversing tool. It was built focused on security issues and tricks that malware researcher have on they every day work. For those situations on where the malware has <b>anti-analysis</b> routines, Droidefense attemps to bypass them in order to get to the code and 'bad boy' routine. Sometimes those techniques can be virtual machine detection, emulator detection, self certificate checking, pipes detection. tracer pid check, and so on.

<b>Droidefense</b> uses an innovative idea in where the code is not decompiled rather than viewed. This allow us to get the global view of the execution workflow of the code with a 100% accuracy on gathered information. With this situation, <b>Droidefense</b> generates a fancy <b>html</b> report with the results for an easy understanding.
</p>

## Usage

### TL;DR

```
java -jar droidefense-cli-1.0-SNAPSHOT.jar -i /path/to/your/sample.apk
```

### Detailed usage

```
java -jar droidefense-cli-1.0-SNAPSHOT.jar

________               .__    .___      _____                            
\______ \_______  ____ |__| __| _/_____/ ____\____   ____   ______ ____  
 |    |  \_  __ \/  _ \|  |/ __ |/ __ \   __\/ __ \ /    \ /  ___// __ \ 
 |    `   \  | \(  <_> )  / /_/ \  ___/|  | \  ___/|   |  \\___ \\  ___/ 
/_______  /__|   \____/|__\____ |\___  >__|  \___  >___|  /____  >\___  >
        \/                     \/    \/          \/     \/     \/     \/ 


	* Current build: 			2017_12_05__12_07_01
	* Check out on Github: 			https://github.com/droidefense/
	* Report your issue: 			https://github.com/droidefense/engine/issues
	* Lead developer: 			@zerjioang

usage: droidefense
 -d,--debug                 print debugging information
 -h,--help                  print this message
 -i,--input <apk>           input .apk to be analyzed
 -o,--output <format>       select prefered output:
                            json
                            json.min
                            html
 -p,--profile               Wait for JVM profiler
 -s,--show                  show generated report after scan
 -u,--unpacker <unpacker>   select prefered unpacker:
                            zip
                            memapktool
 -v,--verbose               be verbose
 -V,--version               show current version information
 
```

## Useful info

* Checkout how to compile new version at:
  * https://github.com/droidefense/engine/wiki/Compilation 
* Checkout report example at:
  * https://github.com/droidefense/engine/wiki/Pornoplayer-report

## Contributing

Everybody is welcome to contribute to **DROIDEFENSE**. Please check out the [**DROIDEFENSE Contribution Steps**](https://github.com/droidefense/engine/blob/develop/CONTRIBUTING.md) for instructions about how to proceed.
  
And any other comments will be very appreciate.


## Citing

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

## License

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Uses GPL license described below

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
