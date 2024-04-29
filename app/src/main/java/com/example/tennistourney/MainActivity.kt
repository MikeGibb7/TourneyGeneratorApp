package com.example.tennistourney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tennistourney.ui.theme.TennisTourneyTheme
import java.lang.Math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TennisTourneyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DefaultPreview()
                }
            }
        }
    }
}

@Composable
fun TextBox(onTextChanged: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onTextChanged(it)
            },
            label = { Text("Player Name") }
        )
}

@Composable
fun MessageList(players: MutableState<List<Player>>) {
    Column {
        players.value.forEach { player ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = player.name)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        val updatedPlayers = players.value.toMutableList()
                        updatedPlayers.remove(player)
                        players.value = updatedPlayers
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
@Composable
fun matchCreator(players: List<Player>): List<Match> {
    // Create a set to store pairs of players already matched
    val matchedPairs = mutableSetOf<Match>()
// Filter out players that are already matched with the current player
    var unmatchedPlayers = players
    // Generate sets of matches for each player
    players.forEach { player ->

        unmatchedPlayers = unmatchedPlayers.filterNot{ it == player }
        // Create sets of matches for the current player with each unmatched player
        unmatchedPlayers.forEach { opponent ->
            matchedPairs.add(Match(player,opponent, null))
        }
    }
    return matchedPairs.toList()
}

@Composable
fun tRows(currentTab: MutableState<Int>) {
    TabRow(selectedTabIndex = currentTab.value) {
        Tab(
            selected = currentTab.value == 0,
            onClick = { currentTab.value = 0 }
        ) {
            Text(text = "Players")
        }
        Tab(
            selected = currentTab.value == 1,
            onClick = { currentTab.value = 1 }
        ) {
            Text(text = "Games")
        }
        Tab(
            selected = currentTab.value == 2,
            onClick = { currentTab.value = 2 }
        ) {
            Text(text = "Standings")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TennisTourneyTheme {
        var playerName by remember {mutableStateOf("")}
        val players = remember { mutableStateOf(listOf<Player>()) }
        val currentTab = remember { mutableStateOf(0) }


        if(currentTab.value == 0) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                tRows(currentTab)
                MessageList(players)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextBox(onTextChanged = { playerName = it })

                    Button(onClick = {
                        players.value = players.value + Player(playerName, 0)
                        playerName = ""
                    }) {
                        Text(text = "Add Player")
                    }
                }
            }
        } else if(currentTab.value == 1){
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                tRows(currentTab)
                val matches = matchCreator(players.value)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    matches.forEach { pair ->
                        var isSelectedPlayer1 by remember { mutableStateOf(false) }
                        var isSelectedPlayer2 by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${pair.player1.name}")
                            Spacer(modifier = Modifier.width(8.dp)) // Add space between the player name and checkbox
                            Checkbox(
                                checked = isSelectedPlayer1,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        isSelectedPlayer2 = false // Deselect the other checkbox
                                        pair.winner = pair.player1
                                        pair.player1.points++
                                        pair.player2.points = max(0, pair.player2.points - 1) // Decrement points of the other player
                                    }
                                    isSelectedPlayer1 = isChecked
                                }
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${pair.player2.name}")
                            Spacer(modifier = Modifier.width(8.dp)) // Add space between the player name and checkbox
                            Checkbox(
                                checked = isSelectedPlayer2,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        isSelectedPlayer1 = false // Deselect the other checkbox
                                        pair.winner = pair.player2
                                        pair.player2.points++
                                        pair.player1.points = max(0, pair.player1.points - 1) // Decrement points of the other player
                                    }
                                    isSelectedPlayer2 = isChecked
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp)) // Add space between the two texts
                    }

                    }
            }
        } else{
            val sortedPlayers = remember { mutableStateOf(listOf<Player>()) }

            sortedPlayers.value = players.value.sortedByDescending { it.points }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                tRows(currentTab)
                sortedPlayers.value.forEach { player ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(text = "Name: ${player.name}")
                        Spacer(modifier = Modifier.width(16.dp)) // Add space between the name and points
                        Text(text = "Points: ${player.points}")
                    }
                }
            }
        }
    }
}