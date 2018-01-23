numdata_test = 6;
count_a = 0;
count_i = 0;
count_u = 0;
count_e = 0;
count_o = 0;

for k = 1: numdata_test
 filename = sprintf('../test/i%d.wav', k);
 i_test_data = wavread(filename);
 c = fix(length(i_test_data)/2);
 i_test_cut = i_test_data(c-127: c+128);
 i_test_rceps = real(ifft(log(abs(fft(i_test_cut)))));
 i_test_cep = i_test_rceps(2:11);

 i2a_dist = norm(a_train_cep - i_test_cep);
 i2i_dist = norm(i_train_cep - i_test_cep);
 i2u_dist = norm(u_train_cep - i_test_cep);
 i2e_dist = norm(e_train_cep - i_test_cep);
 i2o_dist = norm(o_train_cep - i_test_cep);

 min = i2a_dist;
 if min > i2i_dist, min = i2i_dist; end;
 if min > i2u_dist, min = i2u_dist; end;
 if min > i2e_dist, min = i2e_dist; end;
 if min > i2o_dist, min = i2o_dist; end;

 if min == i2a_dist, i_answer = sprintf('%s\n', 'a'); count_a = count_a + 1;
 elseif min == i2i_dist, i_answer = sprintf('%s\n', 'i'); count_i = count_i + 1;
 elseif min == i2u_dist, i_answer = sprintf('%s\n', 'u'); count_u = count_u + 1;
 elseif min == i2e_dist, i_answer = sprintf('%s\n', 'e'); count_e = count_e + 1;
 elseif min == i2o_dist, i_answer = sprintf('%s\n', 'o'); count_o = count_o + 1;
 end;
end;

disp('-------- i test results ---------')
count_a
count_i
count_u
count_e
count_o
correct_i = count_i
error_i = count_a + count_u + count_e + count_o
Acc_rate = (correct_i / numdata_test) * 100
