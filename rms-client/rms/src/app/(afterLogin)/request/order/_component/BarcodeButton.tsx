import style from './barcodeButton.module.css';
import type {RequestWithSelected} from "@/app/(afterLogin)/request/order/_component/OrderTable";
import React from "react";
import BlueButton from "@/app/_component/BlueButton";

interface Props {
    selectRequest: RequestWithSelected[];
}

export default function BarcodeButton({selectRequest}: Props) {
    const handlePrintClick = () => {
        if (selectRequest.length === 0) {
            alert("No selected.");
            return;
        }
        alert(JSON.stringify(selectRequest));
    };

    return (
        <div className={style.container}>
            <BlueButton name={'Print Barcode'} onClick={handlePrintClick}/>
        </div>
    )
}