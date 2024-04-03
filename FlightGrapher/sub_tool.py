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
        ymax_new = ymax * ratio
        ymin_new = ymin * ratio

    else:
        ratio = ylen / aspect / xlen
        xmax_new = xmax * ratio
        xmin_new = xmin * ratio
        ymax_new = ymax
        ymin_new = ymin

    return xmin_new, xmax_new, ymin_new, ymax_new