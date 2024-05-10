'use client'

import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import {DatePicker} from "@mui/x-date-pickers";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";

export default function DatePickerButton()  {
    return <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker
            showDaysOutsideCurrentMonth
            format={'DD/MM/YYYY'}
            sx={{
                "& .MuiInputBase-root.MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
                    borderColor: '#DAE2ED',
                    color: '#1C2025',
                    boxShadow: '0px 2px 2px #F3F6F9',
                    lineHeight: 1.5,
                    borderRadius: 5,
                },
                ".MuiInputBase-root.MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline": {
                    borderColor: "#90BA2D",
                },
                ".MuiInputBase-input.MuiOutlinedInput-input": {
                    padding: 1,
                    paddingLeft: 2,
                    width: 120,
                },
                ".MuiSvgIcon-root": {
                    color: "#c3c3cb",
                },
                "&:hover": {
                    "& .MuiInputBase-root.MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
                        borderColor: "#90BA2D",
                        borderWidth: 1
                    },
                },
            }}
        />
    </LocalizationProvider>
}