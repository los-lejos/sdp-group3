#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Vision subsystem for System Design Project 2014, group 3.
Based on work by group 6, SDP 2013.

Some static functions for saving/loading files.
"""

import sys
import os
import math
import cPickle

__author__ = 'Ingvaras Merkys'

def dump_to_file(obj, path):
    """
    Dump an object to a file specified by path
    """
    path = get_real_path(path)

    # Ensure directories exist
    d = os.path.dirname(path)
    if not os.path.exists(d):
        os.makedirs(d)

    f = open(path, 'w')

    try:
        cPickle.dump(obj, f)
    except Exception, e:
        raise e
    finally:
        f.close()

def load_from_file(path):

    if not file_exists(path):
        return None

    f = open(get_real_path(path), 'r')

    try:
        return cPickle.load(f)
    except Exception, e:
        return None
    finally:
        f.close()

def file_exists(path):
    return os.path.exists(get_real_path(path))

def get_real_path(path):
    """
    Gets a file path relative to the directory that this script is being run from
    """
    return os.path.join(sys.path[0], path)

def euclidean(p1, p2):
    return math.sqrt(math.pow((p1[0] - p2[0]), 2) + math.pow((p1[1] - p2[1]), 2))

