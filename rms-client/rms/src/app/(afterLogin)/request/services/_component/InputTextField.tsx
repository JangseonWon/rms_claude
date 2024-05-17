import {TextField} from "@mui/material";
import {ChangeEvent} from "react";
import {
    useSetAge, useSetMedicalDepartment, useSetMemo, useSetMrn,
    useSetName, useSetPhysician, useSetQuantity, useSetType, useSetWard
} from "@/app/(afterLogin)/request/services/precision-oncology/store/useInputOrderStore";
import {
    useSetSA0001, useSetTA0001, useSetTA0002, useSetTA0003, useSetTA0004, useSetTA0005, useSetTA0007,
    useSetTA0008, useSetTA0009, useSetTA0018, useSetTA0019, useSetTA0020, useSetTA0021, useSetTA0022,
    useSetTA0023, useSetTA0024, useSetTA0025, useSetTA0026, useSetTA0027, useSetTA0090, useSetTA0091,
    useSetTA0092, useSetTA0093, useSetTA0095
} from "@/app/(afterLogin)/request/services/precision-oncology/store/useInputExtensionStore";

interface InputTextFieldProps {
    value: string;
}

export default function InputTextField({ value }: InputTextFieldProps) {

    const setName = useSetName();
    const setMrn = useSetMrn();
    const setAge = useSetAge();
    const setType = useSetType();
    const setQuantity = useSetQuantity();
    const setMemo = useSetMemo();
    const setMedicalDepartment = useSetMedicalDepartment();
    const setWard = useSetWard();
    const setPhysician = useSetPhysician();
    const setSA0001 = useSetSA0001();
    const setTA0001 = useSetTA0001();
    const setTA0002 = useSetTA0002();
    const setTA0003 = useSetTA0003();
    const setTA0004 = useSetTA0004();
    const setTA0005 = useSetTA0005();
    const setTA0007 = useSetTA0007();
    const setTA0008 = useSetTA0008();
    const setTA0009 = useSetTA0009();
    const setTA0018 = useSetTA0018();
    const setTA0019 = useSetTA0019();
    const setTA0020 = useSetTA0020();
    const setTA0021 = useSetTA0021();
    const setTA0022 = useSetTA0022();
    const setTA0023 = useSetTA0023();
    const setTA0024 = useSetTA0024();
    const setTA0025 = useSetTA0025();
    const setTA0026 = useSetTA0026();
    const setTA0027 = useSetTA0027();
    const setTA0090 = useSetTA0090();
    const setTA0091 = useSetTA0091();
    const setTA0092 = useSetTA0092();
    const setTA0093 = useSetTA0093();
    const setTA0095 = useSetTA0095();

    const handleChange = (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const newValue = event.target.value;
        switch (value) {
            case "name":
                setName(newValue);
                break;
            case "mrn":
                setMrn(newValue);
                break;
            case "age":
                setAge(newValue);
                break;
            case "type":
                setType(newValue);
                break;
            case "quantity":
                setQuantity(newValue);
                break;
            case "memo":
                setMemo(newValue);
                break;
            case "medicalDepartment":
                setMedicalDepartment(newValue);
                break;
            case "ward":
                setWard(newValue);
                break;
            case "physician":
                setPhysician(newValue);
                break;
            case "SA0001":
                setSA0001(Number(newValue));
                break;
            case "TA0001":
                setTA0001(Number(newValue));
                break;
            case "TA0002":
                setTA0002(Number(newValue));
                break;
            case "TA0003":
                setTA0003(Number(newValue));
                break;
            case "TA0004":
                setTA0004(Number(newValue));
                break;
            case "TA0005":
                setTA0005(newValue);
                break;
            case "TA0007":
                setTA0007(newValue);
                break;
            case "TA0008":
                setTA0008(Number(newValue));
                break;
            case "TA0009":
                setTA0009(newValue);
                break;
            case "TA0018":
                setTA0018(Number(newValue));
                break;
            case "TA0019":
                setTA0019(Number(newValue));
                break;
            case "TA0020":
                setTA0020(Number(newValue));
                break;
            case "TA0021":
                setTA0021(Number(newValue));
                break;
            case "TA0022":
                setTA0022(Number(newValue));
                break;
            case "TA0023":
                setTA0023(newValue);
                break;
            case "TA0024":
                setTA0024(Number(newValue));
                break;
            case "TA0025":
                setTA0025(Number(newValue));
                break;
            case "TA0026":
                setTA0026(Number(newValue));
                break;
            case "TA0027":
                setTA0027(newValue);
                break;
            case "TA0090":
                setTA0090(newValue);
                break;
            case "TA0091":
                setTA0091(newValue);
                break;
            case "TA0092":
                setTA0092(newValue);
                break;
            case "TA0093":
                setTA0093(newValue);
                break;
            case "TA0095":
                setTA0095(newValue);
                break;
            default:
                break;
        }
    }

    return (
        <TextField
            id="outlined-basic"
            variant="outlined"
            onChange={handleChange}
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
    );
}