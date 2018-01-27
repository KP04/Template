numdata_test = 6;
count_a = 0;
count_i = 0;
count_u = 0;
count_e = 0;
count_o = 0;

for k = 1: numdata_test
 filename = sprintf('../test/e%d.wav', k);
 e_test_data = wavread(filename);
 c = fix(length(e_test_data)/2);
 e_test_cut = e_test_data(c-127: c+128);
 e_test_rceps = real(ifft(log(abs(fft(e_test_cut)))));
 e_test_cep = e_test_rceps(2:11);

 e2a_dist = norm(a_train_cep - e_test_cep);
 e2i_dist = norm(i_train_cep - e_test_cep);
 e2u_dist = norm(u_train_cep - e_test_cep);
 e2e_dist = norm(e_train_cep - e_test_cep);
 e2o_dist = norm(o_train_cep - e_test_cep);

 min = e2a_dist;
 if min > e2i_dist, min = e2i_dist; end;
 if min > e2u_dist, min = e2u_dist; end;
 if min > e2e_dist, min = e2e_dist; end;
 if min > e2o_dist, min = e2o_dist; end;

 if min == e2a_dist, e_answer = sprintf('%s\n', 'a'); count_a = count_a + 1; k
     x = 'a'
 elseif min == e2i_dist, e_answer = sprintf('%s\n', 'i'); count_i = count_i + 1; k
     x = 'i'
 elseif min == e2u_dist, e_answer = sprintf('%s\n', 'u'); count_u = count_u + 1; k
     x = 'u'
 elseif min == e2e_dist, e_answer = sprintf('%s\n', 'e'); count_e = count_e + 1;
 elseif min == e2o_dist, e_answer = sprintf('%s\n', 'o'); count_o = count_o + 1; k
     x = 'o'
 end;
end;

disp('-------- e test results ---------')
count_a
count_i
count_u
count_e
count_o
correct_e = count_e
error_e = count_a + count_i + count_u + count_o
Acc_rate = (correct_e / numdata_test) * 100
