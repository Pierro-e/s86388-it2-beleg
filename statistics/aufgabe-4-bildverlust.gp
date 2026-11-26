set encoding utf8
set terminal svg size 900,550 background rgb "white"
if (!exists("ausgabe")) ausgabe = "statistics/aufgabe-4-bildverlust.svg"
set output ausgabe

set title "Bildverlust ohne Fehlerkorrektur"
set xlabel "Paketverlust im Kanal (%)"
set ylabel "Bildverlust (%)"
set xrange [0:100]
set yrange [0:100]
set xtics 10
set ytics 10
set grid
set key right bottom
set samples 501
set linetype 5 lc rgb "#a6382b"

                                                    # P(X=0) = (1-p)**n
bildverlust(p, n) =100* (1 - (1 - p/100.0)**n)          # 100* P(X≥1) = 100* (1 - P(X=0)) = [0,1]* 100

plot for [n in "1 2 5 10 20"] bildverlust(x, int(n)) title sprintf("%s RTP pro Bild", n) with lines lw 2

unset output
