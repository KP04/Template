    filename = sprintf('../training/a11.wav');
    a_train_data = wavread(filename);
    c = fix(length(a_train_data)/2);
    a_train_cut = a_train_data(c-127: c+128);
    a_train_rceps = real(ifft(log(abs(fft(a_train_cut)))));
    a_train_cep = a_train_rceps(2:11);

    filename = sprintf('../training/i11.wav');
    i_train_data = wavread(filename);
    c = fix(length(i_train_data)/2);
    i_train_cut = i_train_data(c-127: c+128);
    i_train_rceps = real(ifft(log(abs(fft(i_train_cut)))));
    i_train_cep = i_train_rceps(2:11);

    filename = sprintf('../training/u11.wav');
    u_train_data = wavread(filename);
    c = fix(length(u_train_data)/2);
    u_train_cut = u_train_data(c-127: c+128);
    u_train_rceps = real(ifft(log(abs(fft(u_train_cut)))));
    u_train_cep = u_train_rceps(2:11);

    filename = sprintf('../training/e11.wav');
    e_train_data = wavread(filename);
    c = fix(length(e_train_data)/2);
    e_train_cut = e_train_data(c-127: c+128);
    e_train_rceps = real(ifft(log(abs(fft(e_train_cut)))));
    e_train_cep = e_train_rceps(2:11);

    filename = sprintf('../training/o11.wav');
    o_train_data = wavread(filename);
    c = fix(length(o_train_data)/2);
    o_train_cut = o_train_data(c-127: c+128);
    o_train_rceps = real(ifft(log(abs(fft(o_train_cut)))));
    o_train_cep = o_train_rceps(2:11);