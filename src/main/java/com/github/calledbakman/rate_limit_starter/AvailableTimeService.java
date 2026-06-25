package com.github.calledbakman.rate_limit_starter;

import com.github.calledbakman.rate_limit_starter.exception.ServiceNotAvailableException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class AvailableTimeService {

    public void checkAvailability(LocalTime startAt, LocalTime endAt){
            LocalTime currentHour = LocalTime.now();

            if (endAt.isBefore(startAt)) {
                if (endAt.isBefore(currentHour) && startAt.isAfter(currentHour))
                    throw new ServiceNotAvailableException(startAt.toString(), endAt.toString());
            } else if (startAt.isAfter(currentHour) || endAt.isBefore(currentHour))
                throw new ServiceNotAvailableException(startAt.toString(), endAt.toString());
    }

}
