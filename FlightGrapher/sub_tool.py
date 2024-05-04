def get_extent_values(fig, ax, aspect_logo):
    xmin, xmax = ax.get_xlim()
    ymin, ymax = ax.get_ylim()
    ratio_x = (xmax - xmin) / fig.get_figwidth()
    ratio_y = (ymax - ymin) / fig.get_figheight()
    xlen = (xmax - xmin) * 0.3
    ylen = xlen * (ratio_y / ratio_x) * aspect_logo
    xmin_logo = xmax - xlen
    xmax_logo = xmax
    ymin_logo = ymin
    ymax_logo = ymin + ylen
    return xmin_logo, xmax_logo, ymin_logo, ymax_logo

def update_limits(xlim, ylim, aspect):
    xmin, xmax = xlim
    ymin, ymax = ylim

    xlen = xmax - xmin
    ylen = ymax - ymin
    aspect_lim = ylen / xlen
    if aspect_lim < aspect:
        xmax_new = xmax 
        xmin_new = xmin
        ratio = xlen * aspect / ylen
        if ymin == 0.0:
            ymax_new = ymax * ratio
            ymin_new = ymin * ratio

        else:
            ylen *= ratio
            ymid = 0.5 * (ymin + ymax)
            ymax_new = ymid + 0.5 * ylen
            ymin_new = ymid - 0.5 * ylen


    else:
        ratio = ylen / aspect / xlen
        xmid = (xmax + xmin) / 2.0
        xlen *= ratio
        xmax_new = xmid + 0.5 * xlen
        xmin_new = xmid - 0.5 * xlen
        ymax_new = ymax
        ymin_new = ymin

    return xmin_new, xmax_new, ymin_new, ymax_new

def set_limits(ax):

    import numpy as np

    xmin, xmax = ax.get_xlim()
    ymin, ymax = ax.get_ylim()
    zmin, zmax = ax.get_zlim()
    xrange = xmax - xmin
    yrange = ymax - ymin
    zrange = zmax - zmin
    range_max = np.max(np.array([xrange, yrange, zrange]))
    xmin *= range_max / xrange 
    xmax *= range_max / xrange
    ymin *= range_max / yrange 
    ymax *= range_max / yrange
    zmin *= range_max / zrange 
    zmax *= range_max / zrange
    ax.set_xlim(xmin=xmin, xmax=xmax)
    ax.set_ylim(ymin=ymin, ymax=ymax)
    ax.set_zlim(zmin=zmin, zmax=zmax)