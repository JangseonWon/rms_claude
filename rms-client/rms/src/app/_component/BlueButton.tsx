"use client"

import style from "@/app/_component/blueButton.module.css"
import {IconDefinition} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

type Props = {
    name: string
    icon?: IconDefinition
    onClick?: () => void
    disabled?: boolean
}
export default function BlueButton({name, icon, onClick, disabled=false}: Props) {
    return (
        <button
            className={`${style.blueButton} ${disabled ? style.disable : ""}`}
            onClick={disabled ? undefined : onClick}>
            {name}
            {icon && <FontAwesomeIcon icon={icon} style={{ marginLeft: "8px" }} />}
        </button>
    )
}