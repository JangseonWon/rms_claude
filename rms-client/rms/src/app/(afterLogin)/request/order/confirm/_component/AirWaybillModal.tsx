"use client"

import globalStyle from "@/css/modal.module.css";
import style from "@/app/(afterLogin)/request/order/confirm/_component/airWaybillModal.module.css";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import BlueButton from "@/app/_component/BlueButton";
import InputBox from "@/app/_component/InputBox";
import GreenButton from "@/app/_component/GreenButton";
import {useState} from "react";
import {RequestWithSelected} from "@/app/(afterLogin)/request/order/barcode/_component/RequestTable";

type Props = {
    closeModal: () => void;
    selectedRequests: RequestWithSelected[];
    onConfirm: (courierCompany: string, awbNumber: string) => void;
}

export default function AirWaybillModal({closeModal, onConfirm}: Props) {
    const [courierCompany, setCourierCompany] = useState<string>("");
    const [awbNumber, setAwbNumber] = useState<string>("");

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={globalStyle.modalCloseButton}/>
                <div className={style.modalTitle}>AirWaybill</div>
                <div className={style.alignCenter}>
                    <p className={`${style.verticalCenter} ${style.fontBlue}`}>Global courier</p>
                    <InputBox onChange={setCourierCompany} />
                </div>
                <div className={style.alignCenter}>
                    <p className={`${style.verticalCenter} ${style.fontBlue}`}>AirWaybill no.</p>
                    <InputBox onChange={setAwbNumber} />
                </div>
                <div className={style.alignCenter}>
                    <GreenButton
                        name={'Cancel'}
                        onClick={closeModal}
                    />
                    <BlueButton
                        name={"Confirm"}
                        onClick={() => onConfirm(courierCompany, awbNumber)} // Confirm 클릭 시 데이터 전달
                    />
                </div>
            </div>
        </div>
    )
}