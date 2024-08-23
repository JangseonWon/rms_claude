"use client"

import style from "@/app/_component/rectangleButton.module.css";

type Props = {
    name: string
    onClick?: () => void
    disabled?: boolean
}
export default function RectangleButton({name, onClick, disabled=false}: Props) {
    return (
        <button className={`${style.rectangleButton} ${disabled ? style.disable : ""}`} onClick={disabled ? undefined : onClick}>{name}</button>
    )
}