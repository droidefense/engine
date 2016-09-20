
!function( $ ){

  "use strict"

  /* STREAM PUBLIC CLASS DEFINITION
   * ============================= */

   var Stream = function ( content, options ) {
     this.$element = $(content)
     this.options = options

     return this
   }

   Stream.prototype = {

     more: function () {
       loadData.call(this)
     },

     start: function () {
       this.$element.empty()
       loadData.call(this)
     }
  }

 /* STREAM PRIVATE METHODS
  * ===================== */

  function loadData() {

    var url = this.options.url
    var data = this.options.data || null
    var showOn = this.options.show || {}

    if (typeof data == 'string') {
      var matches = data.match(/\{(\w+)\}/)
      if (matches) {
        this.argumentName = matches[1]
        data = data.replace(matches[0], this.options[this.argumentName])
      }
    }
    else {
      var matches = url.match(/\{(\w+)\}/)
      if (matches) {
        this.argumentName = matches[1]
        url = url.replace(matches[0], this.options[this.argumentName])
      }
    }

    if (showOn.error)
      $(showOn.error).hide();

    if (showOn.more)
      $(showOn.more).hide();

    if (showOn.empty)
      $(showOn.empty).hide();

    if (showOn.waiting) {
      var waitAnimationTimeout = setTimeout(function () {
          $(showOn.waiting).show();
      }, 200);
    }

    $.ajax({
      type: this.options.method || 'GET',
      url: url,
      data: data,
      dataType: 'json',
      timeout: this.options.timeout || 50000,
      context: this,
      cache: this.options.cache,
      success: function(data) {

        if (typeof waitAnimationTimeout !== 'undefined')
          clearTimeout(waitAnimationTimeout);

        if (data.html) {
          this.$element.append(data.html)
          if (showOn.notEmpty)
            $(showOn.notEmpty).show()
        }
        else if (!this.$element.html()) {
          if (showOn.empty)
            $(showOn.empty).show()
        }

        if (data.next) {
          this.options[this.argumentName] = data.next

          // turn the scroll monitor back on if option 'auto' is set
          if (this.options.auto)
            $(window).scroll({stream:this}, onScroll);

          if (showOn.more)
            $(showOn.more).show();
        }
        else {
          if (showOn.more)
            $(showOn.more).hide();
        }

        if (showOn.waiting)
          $(showOn.waiting).hide();

        if (showOn.success)
          $(showOn.success).show();

        if (this.options.success)
            this.options.success(data)
      },
      error: function(jqXHR, textStatus, errorThrown) {

        if (typeof waitAnimationTimeout !== 'undefined')
          clearTimeout(waitAnimationTimeout);

        if (showOn.waiting)
          $(showOn.waiting).hide();

        if (showOn.error)
          $(showOn.error).show();

        if (this.options.error)
            this.options.error(errorThrown)
      }
    });
  }

  function onScroll(event) {

    var element = event.data.stream.$element
    var viewBottom = $(window).scrollTop() + $(window).height()
    var elementBottom = element.offset().top + element.height()

    if (viewBottom >= elementBottom) {
      // avoid receiving more scroll events until more data is loaded
      $(window).unbind(event)
      //load more data
      loadData.call(event.data.stream)
    }
  };

 /* PLUGIN DEFINITION
  * ============================ */

  $.fn.stream = function ( options ) {

    var stream = this.data('stream')

    if (!stream) {
      return this.each(function () {
        $(this).data('stream', new Stream(this, options))
      })
    }

    if ( typeof options == 'string' ) {
      stream[options]()
    }
    else {
      stream.options = options
    }

    return this;
  }

}( window.jQuery);