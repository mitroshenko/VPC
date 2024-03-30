package com.mitroshenko.vpc;

import com.groupstp.isdayoff.IsDayOff;
import com.groupstp.isdayoff.IsDayOffDateType;
import com.groupstp.isdayoff.enums.DayType;
import com.groupstp.isdayoff.enums.LocalesType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@RestController
public class VacationPayCalculatorApplication {

    IsDayOff isDayOff = IsDayOff.Builder()
            .setLocale(LocalesType.RUSSIA)
            .build();

    public static void main(String[] args) {
        SpringApplication.run(VacationPayCalculatorApplication.class, args);
    }

    @GetMapping("/calculateBase")
    public String calculateBase(@RequestParam(value = "salary", defaultValue = "") String salaryParam,
                                @RequestParam(value = "days", defaultValue = "") String daysParam) {
        if (salaryParam.length() > 0 && daysParam.length() > 0) {
            try {
                double salary = Double.parseDouble(salaryParam);
                int days = Integer.parseInt(daysParam);

                if (days > 0 & salary > 0) {
                    double vacationPay = salary / 29.3 * days;
                    String vacationPayDouble = new DecimalFormat("#0.00").format(vacationPay);
                    return String.format("Your vacation pay is: %s", vacationPayDouble);
                } else {
                    return "Enter positive values";
                }
            } catch (NumberFormatException e) {
                return "Invalid data format";
            }
        } else {
            return "There is not enough data to calculate";
        }
    }

    @GetMapping("/calculateExtended")
    public String calculateExtended(@RequestParam(value = "salary", defaultValue = "") String salaryParam,
                                    @RequestParam(value = "vacationStartDay", defaultValue = "") String vacationStartDayParam,
                                    @RequestParam(value = "vacationEndDay", defaultValue = "") String vacationEndDayParam
    ) {
        if (salaryParam.length() > 0 && vacationStartDayParam.length() > 0 && vacationEndDayParam.length() > 0) {
            try {
                double salary = Double.parseDouble(salaryParam);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date vacationStartDay = dateFormat.parse(vacationStartDayParam);
                Date vacationEndDay = dateFormat.parse(vacationEndDayParam);

                List<IsDayOffDateType> daysList = isDayOff.daysTypeByRange(vacationStartDay, vacationEndDay);
                if (daysList == null) {
                    return "Check entered dates and enter it correctly";
                }
                int workDaysCounter = 0;
                for (IsDayOffDateType day : daysList) {
                    if (day.getDayType() == DayType.WORKING_DAY) {
                        workDaysCounter++;
                    }
                }

                if (salary > 0) {
                    double vacationPay = salary / 21.5 * workDaysCounter;
                    String vacationPayDouble = new DecimalFormat("#0.00").format(vacationPay);
                    return String.format("Your vacation pay is: %s", vacationPayDouble);
                } else {
                    return "Enter positive value for salary";
                }
            } catch (NumberFormatException e) {
                return "Invalid data format";
            } catch (ParseException e) {
                return "Enter the date in format dd/MM/yyyy";
            }
        } else {
            return "There is not enough data to calculate";
        }
    }
}
