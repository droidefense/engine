This package provides some simple Java classes to read the magic files from Unix to simulate the file(1) command.

For more information, visit the home page:
	http://256stuff.com/sources/simplemagic/

The git repository is:
	https://github.com/j256/simplemagic

Maven packages are published via the central repo:
	http://repo1.maven.org/maven2/com/j256/simplemagic/simplemagic/

Enjoy,
Gray Watson

----------------------------------------------------------------------

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

Once you have the ContentDetails it provides:
 
    * Enumerated type if the type is common
    * Approximate content-name
    * Full message produced by the magic file
    * Mime-type string if one configured by the config file

For example:
    HTML, mime 'text/html', msg 'HTML document text'
    Java, msg 'Java serialization data, version 5'
    PDF, mime 'application/pdf', msg 'PDF document, version 1.4'
    gzip, mime 'application/x-gzip', msg 'gzip compressed data, was "", from Unix...'
    GIF, mime 'image/gif', msg 'GIF image data, version 89a, 16 x 16'
    PNG, mime 'image/png', msg 'PNG image, 600 x 371, 8-bit/color RGB, non-interlaced'
    ISO, mime 'audio/mp4', msg 'ISO Media, MPEG v4 system, iTunes AAC-LC'
    Microsoft, mime 'application/msword', msg 'Microsoft Word Document'
    RIFF, mime 'audio/x-wav', msg 'RIFF (little-endian) data, WAVE audio, Microsoft...'
    JPEG, mime 'image/jpeg', msg 'JPEG image data, JFIF standard 1.01'
