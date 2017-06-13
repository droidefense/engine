Java Simple Magic
=================

Here's a "magic" number package  which allows content-type (mime-type) determination from files and byte arrays. It makes
use of the magic(5) Unix content-type files to implement the same functionality as the Unix file(1) command in Java which
detects the contents of a file. It uses either internal config files or can read ```/etc/magic```,
```/usr/share/file/magic```, or other magic(5) files and determine file content from ```File```, ```InputStream```, or
```byte[]```.

* For more information, visit the [home page](http://256stuff.com/sources/simplemagic/).
* The source code be found on the [git repository](https://github.com/j256/simplemagic).  [![CircleCI](https://circleci.com/gh/j256/simplemagic.svg?style=svg)](https://circleci.com/gh/j256/simplemagic)
* Maven packages are published via the [maven central repo](http://repo1.maven.org/maven2/com/j256/simplemagic/simplemagic/).

Enjoy, Gray Watson

# Getting Started

To get started you use the SimpleMagic package like the following:

	// create a magic utility using the internal magic file
	ContentInfoUtil util = new ContentInfoUtil();
	// if you want to use a different config file(s), you can load them by hand:
	// ContentInfoUtil util = new ContentInfoUtil("/etc/magic");
	...
	ContentInfo info = util.findMatch("/tmp/upload.tmp");
	// or
	ContentInfo info = util.findMatch(inputStream);
	// or
	ContentInfo info = util.findMatch(contentByteArray);

Once you have the [```ContentInfo```](https://github.com/j256/simplemagic/blob/master/src/main/java/com/j256/simplemagic/ContentInfo.java)
it provides:
 
* Enumerated type if the type is common
* Approximate content-name
* Full message produced by the magic file
* Mime-type string if one configured by the config file
* Associated file extensions (if any)

For example:

* ```HTML, mime 'text/html', msg 'HTML document text'```
* ```Java, msg 'Java serialization data, version 5'```
* ```PDF, mime 'application/pdf', msg 'PDF document, version 1.4'```
* ```gzip, mime 'application/x-gzip', msg 'gzip compressed data, was "", from Unix...'```
* ```GIF, mime 'image/gif', msg 'GIF image data, version 89a, 16 x 16'```
* ```PNG, mime 'image/png', msg 'PNG image, 600 x 371, 8-bit/color RGB, non-interlaced'```
* ```ISO, mime 'audio/mp4', msg 'ISO Media, MPEG v4 system, iTunes AAC-LC'```
* ```Microsoft, mime 'application/msword', msg 'Microsoft Word Document'```
* ```RIFF, mime 'audio/x-wav', msg 'RIFF (little-endian) data, WAVE audio, Microsoft...'```
* ```JPEG, mime 'image/jpeg', msg 'JPEG image data, JFIF standard 1.01'```

# ChangeLog Release Notes

See the [ChangeLog.txt file](src/main/javadoc/doc-files/changelog.txt).
