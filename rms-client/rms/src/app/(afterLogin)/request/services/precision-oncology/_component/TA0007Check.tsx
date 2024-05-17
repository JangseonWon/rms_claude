import style from "@/app/(afterLogin)/request/services/precision-oncology/_component/orderModal.module.css";
import {useSetTA0007} from "@/app/(afterLogin)/request/services/precision-oncology/store/useInputExtensionStore";
import {ChangeEvent, useState} from "react";

export default function TA0007Check() {
    const setTA0007 = useSetTA0007();
    const [value, setValue] = useState<string>("");

    const handleTA0007RadioChange = (e: ChangeEvent<HTMLInputElement>) => {
        const selectedValue = e.target.value;
        setValue(selectedValue);
        setTA0007(selectedValue);
    };

    return (
        <div className={style.checkbox}>
            <div className={style.subCheck}>
                <input
                    type="radio"
                    id="double"
                    value="double"
                    checked={value === "double"}
                    onChange={handleTA0007RadioChange}
                />
                <label htmlFor="double" className={style.on}>double</label>
            </div>
            <div className={style.subCheck}>
                <input
                    type="radio"
                    id="triple"
                    value="triple"
                    checked={value === "triple"}
                    onChange={handleTA0007RadioChange}
                />
                <label htmlFor="triple" className={style.on}>triple</label>
            </div>
            <div className={style.subCheck}>
                <input
                    type="radio"
                    id="quad"
                    value="quad"
                    checked={value === "quad"}
                    onChange={handleTA0007RadioChange}
                />
                <label htmlFor="quad" className={style.on}>quad</label>
            </div>
        </div>
    );
}