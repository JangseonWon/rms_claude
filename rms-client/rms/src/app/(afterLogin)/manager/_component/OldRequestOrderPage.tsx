import textboxStyle from "@/app/_component/textBox.module.css";
import style from "@/app/(afterLogin)/manager/_component/managePage.module.css";
import React, {ChangeEvent, useState} from "react";
import BlueButton from "@/app/_component/BlueButton";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {putOldRequest} from "@/app/(afterLogin)/manager/_api/putOldRequest";

const inputValue = [
    {
        "user": {
            "id": "G073287"
        },
        "service": {
            "id": "ON201"
        },
        "create_at": "2025-07-15T14:04:20",
        "sample": {
            "barcode": "202407151230011",
            "extensions": [],
            "patient": {
                "organization": {
                    "id": "OA00035"
                },
                "name": "NGUYEN DAC BAO LAM",
                "serial": "250320-100",
                "birth_year": 2015,
                "birth_month": 12,
                "birth_day": 7,
                "sex": "M"
            },
            "sample_type": {
                "id": "02"
            },
            "sampling_on": "2025-03-20",
            "age": 9,
            "quantity": "0"
        },
        "request_relation": {
            "id": 1
        },
        "ward": "VNCH",
        "physician": "Dr Ha",
        "status": "COMPLETED"
    }
]

export default function OldRequestOrderPage() {
    const showAlert = CallAlertDialog();
    const [orderJson, setOrderJson] = useState<object | null>(null);
    const [textValue, setTextValue] = useState<string>("");

    const handleOnClickOrder = async () => {
        if (!orderJson) {
            showAlert("JSON이 올바르지 않습니다.");
            return;
        }

        const response = await putOldRequest(orderJson);
        if (response.ok) showAlert("Order Completed!");
        else showAlert("Fail");
    };

    const handleInputChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
        const value = e.target.value;
        setTextValue(value);

        try {
            const parsed = JSON.parse(value);
            setOrderJson(parsed);
        } catch (error) {
            setOrderJson(null);
        }
    };

    return (
        <>
            <div className={style.container}>
                <div className={style.header}>
                    Old Request Order Page
                </div>
                <div className={style.buttonSection}>
                    <BlueButton name={"Order"} onClick={handleOnClickOrder}/>
                </div>
                <div className={style.textboxContainer}>
                    <div className={style.textbox}>
                        <p>의뢰에 필요한 json</p>
                        <textarea
                            className={textboxStyle.memo}
                            rows={20}
                            value={textValue}
                            onChange={handleInputChange}
                            placeholder="여기에 JSON을 붙여넣기 하세요"
                        />
                    </div>
                    <div className={style.textboxExample}>
                        <p>json 예시</p>
                        <textarea
                            className={textboxStyle.memo}
                            rows={20}
                            value={JSON.stringify(inputValue, null, 2)}
                            readOnly={true}
                        />
                    </div>
                </div>
            </div>
        </>
    )
}