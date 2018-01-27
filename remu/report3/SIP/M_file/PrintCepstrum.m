function PrintCepstrum(rceps)
    rceps(1) = 0;
    rceps(21 : length(rceps)-21+1) = 0;
    spec = real(fft(rceps));
    t = 0 : length(spec)-1;
    plot(t, spec)
    xlim([0 length(spec)/2])
    