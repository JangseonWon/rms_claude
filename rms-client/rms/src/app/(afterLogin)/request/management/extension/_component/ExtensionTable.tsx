'use client';

import style from "./extensionTable.module.css";
import React, {useEffect, useState} from "react";
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import {getExtensions} from "@/app/(afterLogin)/request/management/extension/_api/getExtensions";
import ExtensionModal from "@/app/(afterLogin)/request/management/extension/_component/ExtensionModal";

export default function ExtensionTable() {
    const [extensionData, setExtensionData] = useState<Extensions[]>([]);
    const [selectExtensionData, setSelectExtensionData] = useState<Extensions | undefined>();
    const [extensionEditModalOpen, setExtensionEditModalOpen] = useState<boolean>(false);
    const [extensionType, setExtensionType] = useState<string>('');

    const fetchData = async () => {
        try {
            const response = await getExtensions()
            const data = await response.json();
            setExtensionData(data as Extensions[]);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setExtensionData([]);
        }
    }

    const handleEditExtensionClick = (extension: Extensions) => {
        setExtensionEditModalOpen(true);
        setSelectExtensionData(extension);
        const type = mapRegexToType(extension.regex);
        setExtensionType(type);
    }

    const closeModal = () => {
        setExtensionEditModalOpen(false);
    }

    const mapRegexToType = (regex: string): string => {
        if (regex === "\\b(?:true|false)\\b") {
            return "Boolean";
        } else if (regex === "-?\\d+(\\.\\d+)?") {
            return "Number";
        } else if (regex === "-?\\d+") {
            return "Number";
        } else if (regex.includes("|")) {
            return "List";
        } else {
            return "String";
        }
    }

    const mapRegexToValue = (regex: string): string => {
        if (regex.includes("|")) {
            return regex
                .replace(/\\b|\b/g, '')
                .replace(/\\|\(|\)|\?:/g, '')
                .split('|')
                .filter(value => value !== 'true' && value !== 'false')
                .join(',');
        }
        return "";
    };

    useEffect(() => {
        fetchData()
    }, []);

    return (
        <>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Type</th>
                        <th>List Value</th>
                        <th>Edit</th>
                    </tr>
                    </thead>
                    <tbody>
                    {extensionData && extensionData.length > 0 && extensionData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.id}</td>
                            <td>{row.name}</td>
                            <td>{mapRegexToType(row.regex)}</td>
                            <td>{mapRegexToValue(row.regex)}</td>
                            <td>
                                <button
                                    className={style.editButton}
                                    onClick={() => handleEditExtensionClick(row)}
                                >
                                    Edit
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
            {extensionEditModalOpen && selectExtensionData && (
                <ExtensionModal
                    getExtension={selectExtensionData}
                    type={extensionType}
                    open={extensionEditModalOpen}
                    closeModal={closeModal}
                    refreshTable={fetchData}
                />
            )}
        </>
    );
}