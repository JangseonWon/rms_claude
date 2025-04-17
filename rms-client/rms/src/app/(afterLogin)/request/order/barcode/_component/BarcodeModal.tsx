"use client"

import globalStyle from "@/css/modal.module.css";
import globalTableStyle from "@/css/globalTable.module.css";
import style from "@/app/(afterLogin)/request/order/barcode/_component/barcodeModal.module.css";
import React, {useState} from "react";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import BlueButton from "@/app/_component/BlueButton";
import {RequestWithSelected} from "@/app/(afterLogin)/request/order/barcode/_component/RequestTable";
import JsPDF from "jspdf";
import "jspdf-barcode";
// @ts-ignore
import {nanumGothicBase64} from "@/app/(afterLogin)/request/order/barcode/_component/nanumGothicBase64";

type Props = {
    requests: RequestWithSelected[]
    closeModal: () => void;
}

export default function BarcodeModal({requests, closeModal}: Props) {
    const [copyCounts, setCopyCounts] = useState(
        requests.reduce((acc, request) => {
            const requestKey = `${request.sample!.id}:${request.service!.id}`
            acc[requestKey] = 1;
            return acc;
        }, {} as { [key: string]: number })
    );
    const handleChangeCopies = (id: string, value: number) => {
        setCopyCounts((prev) => ({
            ...prev,
            [id]: value > 0 ? value : 1,
        }));
    };
    const formatBarcode = (barcode: string) => {
        return `${barcode.slice(0, 8)}-${barcode.slice(8, 11)}-${barcode.slice(11)}`;
    };
    const formatShortBarcode = (barcode: string) => {
        const datePart = barcode.slice(0, 8);

        const startDate = Date.UTC(2000, 0, 1);
        const currentDate = Date.UTC(
            parseInt(datePart.slice(0, 4)),
            parseInt(datePart.slice(4, 6)) - 1,
            parseInt(datePart.slice(6, 8))
        );
        const dayDifference = Math.floor(
            (currentDate - startDate) / (1000 * 60 * 60 * 24)
        );
        const remainingPart = barcode.slice(8);
        return `${dayDifference}${remainingPart}`;
    }
    const handlePrint = () => {
        const doc = new JsPDF() as JsPDF & { barcode: Function };
        const xStart = 20;
        const yStart = 33;
        const xGap = 66;
        const yGap = 34;
        let currentX = xStart;
        let currentY = yStart;
        let printCount = 0;
        doc.addFileToVFS("NanumGothic.ttf", nanumGothicBase64);
        doc.addFont('NanumGothic.ttf', 'NanumGothic', 'normal');
        doc.setFont('NanumGothic');

        requests.forEach((request, index) => {
            const requestKey = `${request.sample!.id}:${request.service!.id}`
            const copies = copyCounts[requestKey] || 1;
            const barcode = formatBarcode(request.sample!.barcode!);
            const shortBarcode = formatShortBarcode(request.sample!.barcode!)
            const patientName = request.sample!.patient!.name!
            const sampleType = request.sample!.sample_type!.name!

            for (let copy = 0; copy < copies; copy++) {
                doc.saveGraphicsState();
                doc.setFontSize(7)
                doc.text(`${shortBarcode}`, currentX + 9, currentY - 5);
                doc.setFontSize(10)
                doc.text(`${barcode}`, currentX, currentY);
                doc.setFontSize(7)
                doc.text(doc.splitTextToSize(`${patientName} [${sampleType}]`, 35), currentX, currentY + 4)
                doc.barcode(shortBarcode, {
                    x: currentX-3,
                    y: currentY-8,
                    fontSize: 35,
                    textColor: "black"
                });
                doc.restoreGraphicsState();
                doc.saveGraphicsState();
                doc.setFontSize(7)
                doc.text(`${shortBarcode}`, currentX+9, currentY + yGap - 5);
                doc.setFontSize(10)
                doc.text(`${barcode}`, currentX, currentY + yGap);
                doc.setFontSize(7)
                doc.text(`${patientName} [${sampleType}]`, currentX, currentY + yGap + 3);
                doc.barcode(shortBarcode, {
                    x: currentX-3,
                    y: currentY + yGap - 8,
                    fontSize: 35,
                    textColor: "black"
                });
                doc.restoreGraphicsState();
                currentX += xGap;
                printCount++;

                if (printCount % 3 === 0) {
                    currentX = xStart;
                    currentY += yGap * 2;
                }
                if (printCount % 12 === 0 && (index !== requests.length - 1 || copy !== copies - 1)) {
                    currentX = xStart;
                    currentY = yStart;
                    doc.addPage();
                }

            }

        });
        const pdfBlob = doc.output("blob");
        const pdfUrl = URL.createObjectURL(pdfBlob);
        window.open(pdfUrl, "_blank");
    };

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={globalStyle.modalCloseButton}/>
                <div className={style.modalTitle}>Barcode Print</div>
                <div className={style.buttonGroup}>
                    <BlueButton name={'Print'} onClick={handlePrint}/>
                </div>
                <div className={style.content}>
                    <table className={globalTableStyle.table}>
                        <thead>
                        <tr>
                            <th style={{width: "90px"}}>Copies</th>
                            <th>Institution Name</th>
                            <th>Registration ID</th>
                            <th>Service</th>
                            <th>Patient(s) Name</th>
                            <th>MRN</th>
                            <th>Speciment type</th>
                        </tr>
                        </thead>
                        <tbody>
                        {requests && requests.length > 0 ? (requests && requests.map(request => {
                            const requestKey = `${request.sample?.id}:${request.service?.id}`
                            return (
                                <tr key={requestKey}>
                                    <td style={{width: "90px"}}>
                                        <div className={style.roundButtonContainer}>
                                            <button onClick={() => {
                                                handleChangeCopies(requestKey, (copyCounts[requestKey] || 1) - 1)
                                            }} className={style.roundButton}>-
                                            </button>
                                            <span className={style.countText}>{copyCounts[requestKey] || 1}</span>
                                            <button onClick={() => {
                                                handleChangeCopies(requestKey, (copyCounts[requestKey] || 1) + 1)
                                            }} className={style.roundButton}>+
                                            </button>
                                        </div>
                                    </td>
                                    <td>{request.sample?.patient?.organization?.name}</td>
                                    <td>{request.sample?.barcode}</td>
                                    <td>{request.service?.name}</td>
                                    <td>{request.sample?.patient?.name}</td>
                                    <td>{request.sample?.patient?.serial}</td>
                                    <td>{request.sample?.sample_type?.name}</td>
                                </tr>
                            )
                        })) : (
                            <tr>
                                <td colSpan={7} className={globalTableStyle.noData}>
                                    The searched data does not exist
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )

}