"use client"

import style from "@/app/(afterLogin)/request/cart/_component/info.module.css"
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useRouter, useSearchParams} from "next/navigation";
import SelectBox from "@/app/_component/SelectBox"
import {useEffect} from "react";
import {getRequest} from "@/app/(afterLogin)/request/cart/_api/getRequest";


export default function Info() {
    const router = useRouter();
    const searchParams = useSearchParams()
    const orderId = searchParams.get("order")
    const serviceId = searchParams.get("service")
    const sampleId = searchParams.get("sample")

    const onClickClose = () => {
        router.back();
    };
    const handleSelectionChange = (selectedValue: string) => {
        console.log(`===============${selectedValue}`)
    };
    useEffect(() => {
        fetchData()
        const handleKeyPress = (event: KeyboardEvent) => {
            if (event.key === 'Escape') {router.back();}
        };
        window.addEventListener('keydown', handleKeyPress);
        return () => {window.removeEventListener('keydown', handleKeyPress);};
    }, [router]);

    const fetchData = async () => {
        const response = await getRequest(orderId!, serviceId!, sampleId!);
        const data = await response.json();
        console.log(`============${data}`)
    };

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <div className={style.modalTitle}>
                    <h1>Order Details</h1>
                    <button onClick={onClickClose}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </button>
                </div>
                <div className={style.modalContent}>
                    <SelectBox label={"Service"} options={[{name: "test"}, {name: "Aa"}]} onSelectionChange={handleSelectionChange}/>
                </div>
            </div>
        </div>
    )
}