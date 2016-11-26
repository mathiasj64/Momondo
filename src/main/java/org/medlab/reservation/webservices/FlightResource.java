package org.medlab.reservation.webservices;

import org.apache.commons.lang.StringUtils;
import org.medlab.reservation.dao.FlightDao;
import org.medlab.reservation.dao.FlightInstanceDao;
import org.medlab.reservation.entity.Airport;
import org.medlab.reservation.entity.Flight;
import org.medlab.reservation.entity.FlightInstance;
import org.medlab.reservation.exceptions.FlightException;
import org.medlab.reservation.exceptions.IllegalInputException;
import org.medlab.reservation.exceptions.NoFlightsException;
import org.medlab.reservation.exceptions.NoTicketsException;
import org.medlab.reservation.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/flights")
public class FlightResource {

    @Autowired
    FlightDao flightDao;
    @Autowired
    FlightInstanceDao flightInstanceDao;

    @GET
    @Path("{from}/{date}/{tickets}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAvailableFlightsFromAndSince(@PathParam("from") String from,
                                           @PathParam("date") String date,
                                           @PathParam("tickets") Integer tickets) {

        if (StringUtils.isEmpty(from) || StringUtils.isEmpty(date) || tickets == null) {
            throw new IllegalInputException();
        }
        Long time;
        try {
            time = DateUtils.dateStringToLong(date);
        } catch (Exception e) {
            throw new IllegalInputException();
        }
        List<FlightInstance> flights = flightInstanceDao.getAllFlightsByAirportSince(from, time);
        if (CollectionUtils.isEmpty(flights)) {
            throw new NoFlightsException();
        }
        ArrayList toRemove = new ArrayList();
        for (FlightInstance flight : flights) {
            if (flight.getAvailableSeats() < tickets) {
                toRemove.add(flight);
            }
        }
        flights.removeAll(toRemove);
        if (CollectionUtils.isEmpty(flights)) {
            throw new NoTicketsException();
        }
        return Response.status(200).entity(flights).build();
    }

    @GET
    @Path("create")
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    public Response save() {

        Airport airport = new Airport();
        airport.setIATACode("NY");
        airport.setName("NY");

        Flight flight = new Flight();
        flight.setFlightNumber("QE1234");
        flight.setAirport(airport);

        FlightInstance flightInstance = new FlightInstance();
        flightInstance.setFlightId("00FZ5");
        flightInstance.setAvailableSeats(145);
        flightInstance.setFlight(flight);

        FlightInstance flight1 = flightInstanceDao.create(flightInstance);

        return Response.status(200).entity(flight1).build();
    }

}
