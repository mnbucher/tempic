/*


    IMPORTANT: This class is still under construction (WIP).


 */

package com.uzh.tempic.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.uzh.tempic.client.TempicServiceAsync;
import com.uzh.tempic.shared.TemperatureData;

import java.util.ArrayList;
import java.util.Arrays;

public class CountryPresenter implements Presenter {
    /**
     * All the UI Elements the Presenter should know about and which must be implemented by the view are defined in the following display interface.
     */
    public interface Display {
        void setCountryNames(ArrayList<String> countryNames);
        void setCityNames(ArrayList<String> cityNames);
        void setTemperatureData(ArrayList<TemperatureData> result);
        HasClickHandlers getFilterButton();
        ListBox getCountryListBox();
        ListBox getCityListBox();
        ListBox getFromYearListBox();
        ListBox getToYearListBox();
        ListBox getUncertaintyListBox();
        ListBox getAggregateListBox();
        ListBox getGroupByCityOrCountryListBox();
        Widget asWidget();
    }

    private final TempicServiceAsync rpcService;
    private final HandlerManager eventBus;
    private final Display display;

    public CountryPresenter(TempicServiceAsync rpcService, HandlerManager eventBus, Display view) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.display = view;
    }

    /*
        Binds the interactions in the view to the presenter / eventbus
     */
    private void bind() {
        display.getFilterButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                /* Since GWT doesn't return more than one element from the list box we need to iterate trough all of the items in the list box */
                ListBox countryListBox = display.getCountryListBox();
                ListBox cityListBox = display.getCityListBox();
                ListBox fromYearListBox = display.getFromYearListBox();
                ListBox toYearListBox = display.getToYearListBox();
                ListBox uncertaintyListBox = display.getUncertaintyListBox();
                ListBox aggregateListBox = display.getAggregateListBox();
                ListBox groupByCityOrCountryListBox = display.getGroupByCityOrCountryListBox();

                ArrayList<String> selectedValues = new ArrayList<>();
                int fromYear = Integer.parseInt(fromYearListBox.getSelectedValue());
                int toYear = Integer.parseInt(toYearListBox.getSelectedValue());
                double uncertainty = Double.parseDouble(uncertaintyListBox.getSelectedValue());
                String aggregateBy = aggregateListBox.getSelectedValue();
                String groupByCityOrCountry = groupByCityOrCountryListBox.getSelectedValue();
                String searchBy = "country";

                if(countryListBox.getSelectedItemText() != null && cityListBox.getSelectedItemText() != null) {
                    Window.alert("Please select either Countries or Cities, but not both");
                    return;
                } else if(countryListBox.getSelectedItemText() != null) {
                    searchBy = "country";
                    for (int i = 0, l = countryListBox.getItemCount(); i < l; i++) {
                        if (countryListBox.isItemSelected(i)) {
                            selectedValues.add(countryListBox.getValue(i));
                        }
                    }
                } else {
                    searchBy = "city";
                    for (int i = 0, l = cityListBox.getItemCount(); i < l; i++) {
                        if (cityListBox.isItemSelected(i)) {
                            selectedValues.add(cityListBox.getValue(i));
                        }
                    }
                }
                if(selectedValues.size() == 0) {
                    Window.alert("Please select at least one country.");
                } else if(toYear < fromYear || fromYear > toYear) {
                    Window.alert("Please select a valid time range.");
                } else {
                    try {
                        fetchTemperatureDataFiltered(selectedValues, searchBy, fromYear, toYear, uncertainty, 50000, aggregateBy, groupByCityOrCountry);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        });
    }
    /*
        Renders the view (adds the view to the root DOM Element specified by the AppController
     */
    public void go(final HasWidgets container) {
        bind();
        container.clear();
        container.add(display.asWidget());
        fetchCountryData();
        fetchCityData();
        fetchTemperatureData();
    }

    /**
     * Calls the TempicService and loads all countries in the database.
     */
    private void fetchCountryData() {
        rpcService.getCountryNames(new AsyncCallback<ArrayList<String>>() {
            public void onSuccess(ArrayList<String> result) {
                // pass the Data to the View
                display.setCountryNames(result);
            }
            public void onFailure(Throwable caught) {
                Window.alert("Unable to fetch the country names.");
            }
        });
    }

    /**
     * Calls the TempicService and loads all countries in the database.
     */
    private void fetchCityData() {
        rpcService.getCityNames(new AsyncCallback<ArrayList<String>>() {
            public void onSuccess(ArrayList<String> result) {
                // pass the Data to the View
                display.setCityNames(result);
            }
            public void onFailure(Throwable caught) {
                Window.alert("Unable to fetch the city names.");
            }
        });
    }

    /**
     * Calls the TempicService with a predefined set of parameters
     * to asynchronously load and display a initial set of data.
     *
     * @pre rpcService != null
     * @post
     */
    private void fetchTemperatureData() {
        ArrayList<String> initialCountries = new ArrayList<String>();
        initialCountries.addAll(Arrays.asList("China", "Chile", "Brazil", "Burma"));
        int limitTo = 50000;
        int maxUncertainty = 100;
        rpcService.getTemperatureDataFiltered(initialCountries, "country", 2013, 2013, maxUncertainty, limitTo, "month", "city", new AsyncCallback<ArrayList<TemperatureData>>() {
            public void onSuccess(ArrayList<TemperatureData> result) {
                display.setTemperatureData(result);
            }
            public void onFailure(Throwable caught) {
                Window.alert("Error: " + caught.getMessage());
            }
        });
    }

    /**
     * Calls the TempicService with the provided parameters and asynchronously
     * loads the corresponding data and - if successful - fills the table
     * otherwise an error message is displayed.
     * @param countries A ArrayList containing all country names
     * @param searchBy
     * @param from The starting year to load
     * @param to The last year to load
     * @param uncertainty The acceptable uncertainty
     * @param limitTo The amount of rows that should be loaded
     * @param aggregateBy Whether the data should be aggregated by year or month (String "month" or "year")
     * @param groupByCityOrCountry Whether the data should be grouped by city or country (String "city" or "country")
     */
    private void fetchTemperatureDataFiltered(ArrayList<String> countries, String searchBy, int from, int to, double uncertainty, int limitTo, String aggregateBy, String groupByCityOrCountry) {
        rpcService.getTemperatureDataFiltered(countries, searchBy, from, to, uncertainty, limitTo, aggregateBy, groupByCityOrCountry, new AsyncCallback<ArrayList<TemperatureData>>() {
            public void onSuccess(ArrayList<TemperatureData> result) {
                display.setTemperatureData(result);
                if(result.size() == 0) {
                    Window.alert("Sorry, we couldn't find any data for the specified filters. Increase the range and try again.");
                }
            }
            public void onFailure(Throwable caught) { Window.alert("An error occurred while fetching the filtered temperature data:" + caught.getMessage()); }
        });
    }
}
