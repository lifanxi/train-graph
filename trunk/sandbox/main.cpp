#pragma comment( linker, "/subsystem:\"windows\" /entry:\"mainCRTStartup\"" )
#include <gtk/gtk.h>
#include <glade/glade.h>

#include "train_route.h"
#include "train.h"

GladeXML *xml;
static GdkPixmap * pixmap = NULL;

void
some_handler(GtkWidget *widget)
{
    /* a handler referenced by the glade file.  Must not be static
     * so that it appears in the global symbol table. */
}

extern "C" G_MODULE_EXPORT gint OnButton1Click(GtkWidget * widget, gpointer data)
{
    g_print("Test\n");
    GtkWidget * drawingArea = glade_xml_get_widget(xml, "drawingarea1");
    if (drawingArea)
    {
        g_print("Draw\n");
        gdk_draw_rectangle(pixmap, widget->style->black_gc, TRUE, 10, 10, 100, 100);

        GdkRectangle rect;
        rect.x = 10;
        rect.y = 10;
        rect.width = 100;
        rect.height = 100;

        gtk_widget_draw(widget, &rect);
    }
    return TRUE;
    
}
int TimeToTick(string time)
{
    int hour = (time[0] - '0') * 10 + time[1] - '0';
    int min = (time[3] - '0') * 10 + time[4] - '0';
    
    hour = (hour + 24 - 18) % 24;
   
    return (hour * 60) + min;
}
extern "C" G_MODULE_EXPORT gint on_button2_clicked(GtkWidget * widget, gpointer data)
{
    g_print("Load\n");
//    GtkWidget * drawArea = (GtkWidget *) data;
    GtkWidget * drawArea = glade_xml_get_widget(xml, "drawingarea1");
    // Load Data
    TrainRoute tr;
    tr.ReadFromFile("hn.txt");
    g_print("%s\n", tr.Name.c_str());
    g_print("%f\n", tr.Length);
    for (unsigned int i = 0; i < tr.Stations.size(); ++i)
    {
        g_print("%s,%f\n", tr.Stations[i].Name.c_str(), tr.Stations[i].Pos); //, tr.Stations[i].Level, tr.Stations[i].IsHidden);
    }
    Train t;
    t.ReadFromFile("1462.txt");
    for (unsigned int i = 0; i < t.Detail.size(); ++i)
    {
        g_print("%s,%s,%s\n", t.Detail[i].Arrival.c_str(), t.Detail[i].Departural.c_str(), t.Detail[i].Station.c_str()); 
    }
    // Draw
    // Draw H
    int stepY = 2;
    int stepX = 2;
    int leftMargin = 0;
    int topMargin = 100;
    int ptPerHour = 60;
    int leftStationWidth = 100;
    for (unsigned int i = 0; i < tr.Stations.size(); ++i)
    {
        if (!tr.Stations[i].IsHidden)
        {
            gdk_draw_line(pixmap, drawArea->style->black_gc, 
                          leftMargin + 0, topMargin + tr.Stations[i].Pos * stepY, 
                          leftMargin + leftStationWidth + ptPerHour * 24 * stepX, topMargin + tr.Stations[i].Pos * stepY);
            if (tr.Stations[i].Level <= 2)
            {
                gdk_draw_line(pixmap, drawArea->style->black_gc, 
                              leftMargin + 0, topMargin + tr.Stations[i].Pos * stepY+1, 
                              leftMargin + leftStationWidth + ptPerHour * 24 * stepX, + topMargin + tr.Stations[i].Pos * stepY+1);
            }
            PangoLayout * layout = gtk_widget_create_pango_layout(widget, tr.Stations[i].Name.c_str());
            gdk_draw_layout(pixmap, drawArea->style->black_gc, 
                            leftMargin + 0, topMargin + tr.Stations[i].Pos * stepY - 15, layout);
        }
    }


    // Draw V
    for (int i = 0; i < 24; ++i)
    {
        gdk_draw_line(pixmap, drawArea->style->black_gc,
                      leftMargin + leftStationWidth + i * ptPerHour, topMargin,
                      leftMargin + leftStationWidth + i * ptPerHour, topMargin + tr.Length * stepY);
        gdk_draw_line(pixmap, drawArea->style->black_gc,
                      leftMargin + leftStationWidth + i * ptPerHour + 1, topMargin,
                      leftMargin + leftStationWidth + i * ptPerHour + 1, topMargin + tr.Length * stepY);
        for (int j = 0; j < 6; ++j)
        {
            gdk_draw_line(pixmap, drawArea->style->black_gc,
                          leftMargin + leftStationWidth + i * ptPerHour + j * ptPerHour / 6, topMargin,
                          leftMargin + leftStationWidth + i * ptPerHour + j * ptPerHour / 6, topMargin + tr.Length * stepY);
        }

    }


    // Draw Route
    int lastXa = -1;
    int lastXd = -1; 
    int lastY = -1;

    for (int i = 0; i < t.Detail.size(); ++i)
    {
        string station = t.Detail[i].Station;
        for (int j = 0; j < tr.Stations.size(); ++j)
        {
            if (station == tr.Stations[j].Name)
            {
                int xa = TimeToTick(t.Detail[i].Arrival);
                int xd = TimeToTick(t.Detail[i].Departural);
                g_print("%d,%d\n", xa, xd);
                int y = topMargin + tr.Stations[j].Pos * stepY;

                if (lastXa != -1)
                {
                    gdk_draw_line(pixmap, drawArea->style->black_gc, 
                                  leftMargin + leftStationWidth + lastXd * ptPerHour / 60, lastY,
                                  leftMargin + leftStationWidth + xa * ptPerHour / 60, y);
                    
                }
                if (xa != xd)
                {
                    gdk_draw_line(pixmap, drawArea->style->black_gc, 
                                  leftMargin + leftStationWidth + xa * ptPerHour / 60, y,
                                  leftMargin + leftStationWidth + xd * ptPerHour / 60, y);
                }       
                lastXa = xa;
                lastXd = xd;
                lastY = y;
                break;
            }
        }
    }
//    gtk_widget_queue_draw(drawArea);   
    g_print("%d\n", drawArea->window);
    g_print("%d\n", drawArea);

    gdk_window_invalidate_rect(drawArea->window, NULL, FALSE);
    gdk_window_process_updates (drawArea->window, FALSE);
    return TRUE;
}

extern "C"  G_MODULE_EXPORT gint configure_event(GtkWidget * widget, GdkEventConfigure * event)
{
    g_print("configure_event%d\n", widget->window);
    if (pixmap)
    {
        gdk_pixmap_unref(pixmap);
    }

    pixmap = gdk_pixmap_new(widget->window, widget->allocation.width, widget->allocation.height, -1);
    gdk_draw_rectangle(pixmap, widget->style->white_gc, TRUE, 0, 0, widget->allocation.width, widget->allocation.height);
	return FALSE;

}
extern "C"  G_MODULE_EXPORT gint expose_event(GtkWidget * widget, GdkEventExpose * event)
{
    g_print("expose_event %d\n", widget->window);
    gdk_draw_pixmap(widget->window, widget->style->fg_gc[GTK_WIDGET_STATE(widget)],
                    pixmap,
                    event->area.x, event->area.y,
                    event->area.x, event->area.y,
                    event->area.width, event->area.height);
    return FALSE;
}

int
main(int argc, char **argv)
{
    GtkWidget *widget;

    gtk_init(&argc, &argv);
    xml = glade_xml_new("main.glade", NULL, NULL);

    /* get a widget (useful if you want to change something) */
    widget = glade_xml_get_widget(xml, "window1");

    /* connect signal handlers */
    glade_xml_signal_autoconnect(xml);

    gtk_widget_show_all(widget);

    gtk_main();

    return 0;
}
