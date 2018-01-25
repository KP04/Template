a_total_rceps = zeros(256, 1);
i_total_rceps = zeros(256, 1);
u_total_rceps = zeros(256, 1);
e_total_rceps = zeros(256, 1);
o_total_rceps = zeros(256, 1);

for k = 1 : 12
    if k <= 9
        filename = sprintf('../training/a0%d.wav', k);
        a_train_data = wavread(filename);
        c = fix(length(a_train_data)/2);
        a_train_cut = a_train_data(c-127: c+128);
        a_total_rceps = a_total_rceps + real(ifft(log(abs(fft(a_train_cut)))));

        filename = sprintf('../training/i0%d.wav', k);
        i_train_data = wavread(filename);
        c = fix(length(i_train_data)/2);
        i_train_cut = i_train_data(c-127: c+128);
        i_total_rceps = i_total_rceps + real(ifft(log(abs(fft(i_train_cut)))));

        filename = sprintf('../training/u0%d.wav', k);
        u_train_data = wavread(filename);
        c = fix(length(u_train_data)/2);
        u_train_cut = u_train_data(c-127: c+128);
        u_total_rceps = u_total_rceps + real(ifft(log(abs(fft(u_train_cut)))));
    
        filename = sprintf('../training/e0%d.wav', k);
        e_train_data = wavread(filename);
        c = fix(length(e_train_data)/2);
            e_train_cut = e_train_data(c-127: c+128);
        e_total_rceps = e_total_rceps + real(ifft(log(abs(fft(e_train_cut)))));

        filename = sprintf('../training/o0%d.wav', k);
        o_train_data = wavread(filename);
        c = fix(length(o_train_data)/2);
        o_train_cut = o_train_data(c-127: c+128);
        o_total_rceps = o_total_rceps + real(ifft(log(abs(fft(o_train_cut)))));
    
    else
        filename = sprintf('../training/a%d.wav', k);
        a_train_data = wavread(filename);
        c = fix(length(a_train_data)/2);
        a_train_cut = a_train_data(c-127: c+128);
        a_total_rceps = a_total_rceps + real(ifft(log(abs(fft(a_train_cut)))));

        filename = sprintf('../training/i%d.wav', k);
        i_train_data = wavread(filename);
        c = fix(length(i_train_data)/2);
        i_train_cut = i_train_data(c-127: c+128);
        i_total_rceps = i_total_rceps + real(ifft(log(abs(fft(i_train_cut)))));

        filename = sprintf('../training/u%d.wav', k);
        u_train_data = wavread(filename);
        c = fix(length(u_train_data)/2);
        u_train_cut = u_train_data(c-127: c+128);
        u_total_rceps = u_total_rceps + real(ifft(log(abs(fft(u_train_cut)))));
    
        filename = sprintf('../training/e%d.wav', k);
        e_train_data = wavread(filename);
        c = fix(length(e_train_data)/2);
            e_train_cut = e_train_data(c-127: c+128);
        e_total_rceps = e_total_rceps + real(ifft(log(abs(fft(e_train_cut)))));

        filename = sprintf('../training/o%d.wav', k);
        o_train_data = wavread(filename);
        c = fix(length(o_train_data)/2);
        o_train_cut = o_train_data(c-127: c+128);
        o_total_rceps = o_total_rceps + real(ifft(log(abs(fft(o_train_cut)))));
    
        
    end;
end;

a_train_rceps = a_total_rceps / 12;
a_train_cep = a_train_rceps(2:11);

i_train_rceps = a_total_rceps / 12;
i_train_cep = a_train_rceps(2:11);

u_train_rceps = a_total_rceps / 12;
u_train_cep = a_train_rceps(2:11);

e_train_rceps = a_total_rceps / 12;
e_train_cep = a_train_rceps(2:11);

o_train_rceps = a_total_rceps / 12;
o_train_cep = a_train_rceps(2:11);