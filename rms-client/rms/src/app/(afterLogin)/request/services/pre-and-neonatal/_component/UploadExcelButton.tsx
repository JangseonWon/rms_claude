'use client';

import style from "@/app/(afterLogin)/request/services/pre-and-neonatal/_component/uploadExcelButton.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { ChangeEvent, useEffect, useState, DragEvent } from "react";
import * as XLSX from "xlsx";
import { faUpload, faFileExcel, faArrowUpFromBracket, faXmark } from "@fortawesome/free-solid-svg-icons";

type UploadExcelButtonProps = {
    onFileUpload: (data: any[][]) => void;
};

export default function UploadExcelButton({ onFileUpload }: UploadExcelButtonProps) {
    const [modalOpen, setModalOpen] = useState(false);
    const [dragging, setDragging] = useState(false);
    const [file, setFile] = useState<File | null>(null);

    useEffect(() => {
        const handleKeyDown = (event: KeyboardEvent) => {
            if (event.key === 'Escape') {
                closeModal();
            }
        };
        document.addEventListener('keydown', handleKeyDown);
        return () => {
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, []);

    const openModal = () => {
        setModalOpen(true);
    };

    const closeModal = () => {
        setModalOpen(false);
        setFile(null);
    };

    const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        const selectedFile = e.target.files?.[0];
        if (selectedFile) {
            setFile(selectedFile);
        }
        e.target.value = '';
    };

    const handleDragOver = (e: DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        e.stopPropagation();
        setDragging(true);
    };

    const handleDragLeave = (e: DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        e.stopPropagation();
        setDragging(false);
    };

    const handleDrop = (e: DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        e.stopPropagation();
        setDragging(false);
        const droppedFile = e.dataTransfer.files?.[0];
        if (droppedFile) {
            setFile(droppedFile);
        }
    };

    const processFile = () => {
        if (file) {
            const reader = new FileReader();
            reader.onload = (event) => {
                const data = new Uint8Array(event.target?.result as ArrayBuffer);
                const workbook = XLSX.read(data, { type: 'array' });
                const sheetName = workbook.SheetNames[0];
                const worksheet = workbook.Sheets[sheetName];
                const jsonData: (string | number)[][] = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

                const convertExcelDate = (excelDate: number) => {
                    const date = new Date(Math.round((excelDate - 25569) * 86400 * 1000));
                    return date.toISOString().split('T')[0];
                };

                const transformedData = jsonData.map((row, rowIndex) =>
                    row.map((cell, colIndex) => {
                        const header = jsonData[0][colIndex];
                        if (rowIndex !== 0 &&
                            (header === 'collectionDate' || header === 'patientBOD') &&
                            typeof cell === 'number' && cell > 25569) {
                            return convertExcelDate(cell);
                        }
                        return cell;
                    })
                );
                onFileUpload(transformedData);
                closeModal();
            };
            reader.readAsArrayBuffer(file);
        }
    };

    const handleUploadClick = () => {
        document.getElementById('excelFileInput')?.click();
    };

    return (
        <div>
            <button
                className={style.upload}
                onClick={openModal}>
                Upload Excel&nbsp;
                <FontAwesomeIcon className={style.downloadIcon} icon={faUpload} />
            </button>

            {modalOpen && (
                <div className={style.modalBackground}>
                    <div className={style.modal}>
                        <section className={style.modalTop}>
                            <div>Upload files</div>
                            <div className={style.modalClose} onClick={closeModal}>
                                <FontAwesomeIcon icon={faXmark} />
                            </div>
                        </section>
                        <div
                            className={`${style.modalBody} ${dragging ? style.dragging : ''}`}
                            onDragOver={handleDragOver}
                            onDragLeave={handleDragLeave}
                            onDrop={handleDrop}
                        >
                            {!file ? (
                                <>
                                    <FontAwesomeIcon style={{ fontSize: '40px' }} icon={faArrowUpFromBracket} />
                                    <div className={style.word}>Drag and drop</div>
                                    <div className={style.selectLink}>
                                        <div>or&nbsp;</div>
                                        <>
                                            <input
                                                type="file"
                                                id="excelFileInput"
                                                accept=".xlsx, .xls"
                                                style={{ display: 'none' }}
                                                onChange={handleFileChange}
                                            />
                                            <div className={style.link} onClick={handleUploadClick}>Select file</div>
                                        </>
                                    </div>
                                </>
                            ) : (
                                <>
                                    <FontAwesomeIcon style={{ fontSize: '40px', marginBottom: '10px' }} icon={faFileExcel} />
                                    <div>{file.name}</div>
                                </>
                            )}
                        </div>
                        <section className={style.modalBottom}>
                            <button className={style.button} onClick={processFile}>Confirm</button>
                            <button className={style.button} onClick={closeModal}>Cancel</button>
                        </section>
                    </div>
                </div>
            )}
        </div>
    );
}
