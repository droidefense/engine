PKG = simplemagic
DOCS = $(PKG).html $(PKG).pdf
TEXI = $(PKG).texi
REMOTE ?= doc-host
REMOTE_DIR = remote_texi

all : $(DOCS)

install : $(DOCS)
	cp $(PKG).html $(PKG).pdf ../javadoc/doc-files

clean :
	rm -rf $(DOCS)

$(PKG).html : $(TEXI)
	rm -f $@
	texi2html -number -monolithic $<

$(PKG).pdf : $(TEXI)
	scp $< $(REMOTE):$(REMOTE_DIR)
	ssh $(REMOTE) "(cd $(REMOTE_DIR) ; make $(PKG).pdf)"
	scp $(REMOTE):$(REMOTE_DIR)/$(PKG).pdf .

$(PKG).info : $(TEXI)
	rm -f $@
	makeinfo --no-split $<
