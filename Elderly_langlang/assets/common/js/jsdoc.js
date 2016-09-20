<script type="text/javascript" charset="utf-8">
///<var templateData = [0,0,0,0,0,0,0,0,0,0,0,0];
///<var actualData = [0,0,0,0,0,0,0,0,0,0,0,0];

var templateData = [12,24,42,23,23,13,14,25,26,25,17,19];
var actualData = [15,29,45,29,20,14,19,20,22,26,18,12];

       window.onload = function () {
           var r = Raphael("main"),
               fin = function () {
                   this.flag = r.popup(this.bar.x, this.bar.y, this.bar.value || "0").insertBefore(this);
               },
               fout = function () {
                   this.flag.animate({opacity: 0}, 300, function () {this.remove();});
               },
               fin2 = function () {
                   var y = [], res = [];
                   for (var i = this.bars.length; i--;) {
                       y.push(this.bars[i].y);
                       res.push(this.bars[i].value || "0");
                   }
                   this.flag = r.popup(this.bars[0].x, Math.min.apply(Math, y), res.join(", ")).insertBefore(this);
               },
               fout2 = function () {
                   this.flag.animate({opacity: 0}, 300, function () {this.remove();});
               },
               txtattr = { font: "12px 'Fontin Sans', Fontin-Sans, sans-serif" };
           
           // r.text(160, 10, "Single Series Chart").attr(txtattr);
           //r.text(480, 10, "Multiline Series Stacked Chart").attr(txtattr);
           //r.text(160, 250, "Multiple Series Chart").attr(txtattr);
           r.text(480, 250, "").attr(txtattr);
           
           //r.barchart(10, 10, 300, 220, [[55, 20, 13, 32, 5, 1, 2, 10]]).hover(fin, fout);
           // r.hbarchart(330, 10, 300, 220, [[55, 20, 13, 32, 5, 1, 2, 10], [10, 2, 1, 5, 32, 13, 20, 55]], {stacked: true}).hover(fin, fout);
           //r.hbarchart(10, 250, 300, 220, [[55, 20, 13, 32, 5, 1, 2, 10], [10, 2, 1, 5, 32, 13, 20, 55]]).hover(fin, fout);
           //<var c = r.barchart(150, 0, 600, 370, [[55, 20, 13, 32, 5, 1, 2, 10], [10, 2, 1, 5, 32, 13, 20, 55]], {stacked: true, type: "soft"}).hoverColumn(fin2, fout2);
           var c = r.barchart(150, 45, 600, 325, [templateData, actualData], {stacked: true, type: "soft"}).hoverColumn(fin2, fout2);
			 var x, y = 360;
			 var constArray = [60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170];
			 for (i = 0; i < 12; i++) {
				x = c.bars[0][i].x;
				r.text(x, y, constArray[i]).attr(txtattr);
			 }
       };
</script>