reset
set terminal jpeg
set output "benchmark.jpg"
set style fill solid 1.00 border 0
set style histogram
set style data histogram
set xtics rotate by -45
set grid ytics linestyle 1
set xlabel "Conversant Disruptor vs Competition (Intel Xeon - Broadwell)" font "bold"
set ylabel "time (ms)" font "bold"
plot "performance.dat" using 2:xtic(1) ti "1M Transactions" linecolor rgb "#0066FF"
