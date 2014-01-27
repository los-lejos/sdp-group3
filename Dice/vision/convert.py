def hsvToHsv(h, s, v, h_err=20.0, s_err=30.0, v_err=30.0):
    h1 = int((h + 0.0)*0.71-20.0) % 180
    s1 = int(s*2.55-40.0) % 255
    v1 = int(v*2.55-30.0) % 255
    h2 = int((h + 0.0)*0.71+20.0) % 180
    s2 = int(s*2.55+40.0) % 255
    v2 = int(v*2.55+30.0) % 255
    return ([h1,s1,v1],[h2,s2,v2])
