%{
    testデータとtrainデータのスペクトルを画像に
    各母音の学習データ１２個を平均する

    音声データのプロット
        a1 = wavread('a1.wav');
        plot(a1);

    音声データの中心256点を切り出す
        c = fix(length(a1)/2);
        a1_c = a1(c-127 : c+128);
    
        t = 0 : length(a1_c);
        plot(t, a1_c);
        xlim([0 length(a1_c)-1]);
%}

function PrintSpectrum(file)
    data = wavread(file);
    
    c = fix(length(data)/2);
    data_c = data(c-127 : c+128);
    t = 0 : length(data_c)-1;
    plot(t, data_c)
    xlim([0 length(data_c)-1])
    