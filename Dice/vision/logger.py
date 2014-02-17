import time

class Logger:

    def __init__(self, file_name):
        self._file_name = file_name

    def log(self, message):
        f = open(self._file_name, 'a')
        f.write('{0} {1}\n'.format(time.strftime("%Y-%m-%d %H:%M:%S"), message))
        f.close()
