import {TextField} from "@mui/material";

export default function InputTextField() {
    return <TextField
        id="outlined-basic"
        variant="outlined"
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
                width: 170,
            },
            "&:hover": {
                "& .MuiInputBase-root.MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
                    borderColor: "#90BA2D",
                    borderWidth: 1
                },
            },
        }}
    />
}